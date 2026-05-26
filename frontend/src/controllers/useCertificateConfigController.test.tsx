/* @vitest-environment jsdom */
import { act, type FormEvent } from 'react'
import { createRoot, type Root } from 'react-dom/client'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { defaultCertificateConfig } from '../models/CertificateConfig'
import { startCertificateProcess, type ProcessResponse } from '../services/certificateApi'
import { useCertificateConfigController } from './useCertificateConfigController'

;(globalThis as typeof globalThis & { IS_REACT_ACT_ENVIRONMENT: boolean }).IS_REACT_ACT_ENVIRONMENT = true

vi.mock('../services/certificateApi', () => ({
  startCertificateProcess: vi.fn()
}))

const startCertificateProcessMock = vi.mocked(startCertificateProcess)

const successfulResult: ProcessResponse = {
  rowsRead: 2,
  rowsImported: 2,
  rowsSkipped: 0,
  studentsCreated: 2,
  takesCreated: 2,
  responsesCreated: 6,
  responsesUpdated: 0,
  certificatesGenerated: 2,
  errors: []
}

let root: Root
let container: HTMLDivElement
let controller: ReturnType<typeof useCertificateConfigController>

function TestHarness({ onRender }: { onRender: (current: ReturnType<typeof useCertificateConfigController>) => void }) {
  const currentController = useCertificateConfigController()
  onRender(currentController)
  return null
}

function submitEvent() {
  return {
    preventDefault: vi.fn()
  } as unknown as FormEvent<HTMLFormElement>
}

async function renderController() {
  container = document.createElement('div')
  document.body.append(container)
  root = createRoot(container)

  await act(async () => {
    root.render(<TestHarness onRender={(currentController) => {
      controller = currentController
    }} />)
  })
}

describe('useCertificateConfigController', () => {
  afterEach(() => {
    act(() => {
      root?.unmount()
    })
    container?.remove()
    startCertificateProcessMock.mockReset()
  })

  it('starts with the default config and idle state', async () => {
    await renderController()

    expect(controller.config).toEqual(defaultCertificateConfig)
    expect(controller.status).toBe('idle')
    expect(controller.errorMessage).toBe('')
    expect(controller.importErrors).toEqual([])
    expect(controller.processResult).toBeNull()
  })

  it('updates a single config field and allows an empty string boundary value', async () => {
    await renderController()

    act(() => {
      controller.updateField('certificateTemplate', '')
    })

    expect(controller.config).toEqual({
      ...defaultCertificateConfig,
      certificateTemplate: ''
    })
  })

  it('submits the current config and moves to processed when there are no import errors', async () => {
    await renderController()
    startCertificateProcessMock.mockResolvedValue(successfulResult)

    act(() => {
      controller.updateField('formAnswersName', 'custom.xlsx')
    })

    const event = submitEvent()
    await act(async () => {
      await controller.handleSubmit(event)
    })

    expect(event.preventDefault).toHaveBeenCalled()
    expect(startCertificateProcessMock).toHaveBeenCalledWith({
      ...defaultCertificateConfig,
      formAnswersName: 'custom.xlsx'
    })
    expect(controller.status).toBe('processed')
    expect(controller.processResult).toEqual(successfulResult)
    expect(controller.errorMessage).toBe('')
    expect(controller.importErrors).toEqual([])
  })

  it('moves to error and exposes import errors when processing returns row errors', async () => {
    await renderController()
    const resultWithErrors: ProcessResponse = {
      ...successfulResult,
      errors: [
        {
          field: 'studentEmail',
          message: 'Missing email'
        }
      ]
    }
    startCertificateProcessMock.mockResolvedValue(resultWithErrors)

    await act(async () => {
      await controller.handleSubmit(submitEvent())
    })

    expect(controller.status).toBe('error')
    expect(controller.processResult).toEqual(resultWithErrors)
    expect(controller.errorMessage).toBe('A importacao terminou com erros.')
    expect(controller.importErrors).toEqual(resultWithErrors.errors)
  })

  it('moves to error with the thrown message when the service rejects with an Error', async () => {
    await renderController()
    startCertificateProcessMock.mockRejectedValue(new Error('Template not found'))

    await act(async () => {
      await controller.handleSubmit(submitEvent())
    })

    expect(controller.status).toBe('error')
    expect(controller.errorMessage).toBe('Template not found')
    expect(controller.processResult).toBeNull()
    expect(controller.importErrors).toEqual([])
  })

  it('moves to error with a generic message when the service rejects without an Error', async () => {
    await renderController()
    startCertificateProcessMock.mockRejectedValue('network failed')

    await act(async () => {
      await controller.handleSubmit(submitEvent())
    })

    expect(controller.status).toBe('error')
    expect(controller.errorMessage).toBe('Unexpected error')
    expect(controller.processResult).toBeNull()
    expect(controller.importErrors).toEqual([])
  })
})
