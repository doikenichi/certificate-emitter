/* @vitest-environment jsdom */
import { act, type ComponentProps } from 'react'
import { createRoot, type Root } from 'react-dom/client'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { defaultCertificateConfig } from '../models/CertificateConfig'
import { CertificateConfigView } from './CertificateConfigView'

;(globalThis as typeof globalThis & { IS_REACT_ACT_ENVIRONMENT: boolean }).IS_REACT_ACT_ENVIRONMENT = true

type Props = ComponentProps<typeof CertificateConfigView>

const processResult: NonNullable<Props['processResult']> = {
  rowsRead: 0,
  rowsImported: 0,
  rowsSkipped: 0,
  studentsCreated: 0,
  takesCreated: 0,
  responsesCreated: 0,
  responsesUpdated: 0,
  certificatesGenerated: 0,
  errors: []
}

let root: Root
let container: HTMLDivElement

function defaultProps(overrides: Partial<Props> = {}): Props {
  return {
    config: defaultCertificateConfig,
    errorMessage: '',
    importErrors: [],
    processResult: null,
    status: 'idle',
    onFieldChange: vi.fn(),
    onSubmit: vi.fn(),
    ...overrides
  }
}

async function renderView(props: Props) {
  container = document.createElement('div')
  document.body.append(container)
  root = createRoot(container)

  await act(async () => {
    root.render(<CertificateConfigView {...props} />)
  })
}

function inputAt(index: number) {
  const input = container.querySelectorAll<HTMLInputElement>('input')[index]

  if (!input) {
    throw new Error(`Input ${index} was not rendered`)
  }

  return input
}

function changeInput(input: HTMLInputElement, value: string) {
  const valueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value')?.set
  valueSetter?.call(input, value)
  input.dispatchEvent(new Event('input', { bubbles: true }))
}

describe('CertificateConfigView', () => {
  afterEach(() => {
    act(() => {
      root?.unmount()
    })
    container?.remove()
  })

  it('renders the editable certificate config form and submits it', async () => {
    const props = defaultProps()
    await renderView(props)

    expect(container.textContent).toContain('Issue Certificates')
    expect(inputAt(0).value).toBe(defaultCertificateConfig.formAnswersName)
    expect(inputAt(1).value).toBe(defaultCertificateConfig.certificateTemplate)
    expect(inputAt(2).value).toBe(defaultCertificateConfig.emailTemplateName)

    act(() => {
      changeInput(inputAt(1), 'updated-template.docx')
    })

    expect(props.onFieldChange).toHaveBeenCalledWith('certificateTemplate', 'updated-template.docx')

    const form = container.querySelector('form')
    expect(form).not.toBeNull()

    act(() => {
      form?.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }))
    })

    expect(props.onSubmit).toHaveBeenCalled()
  })

  it('disables the submit button while certificates are running', async () => {
    await renderView(defaultProps({ status: 'running' }))

    const submit = container.querySelector<HTMLButtonElement>('button.submit')

    expect(submit?.disabled).toBe(true)
    expect(submit?.textContent).toBe('Issuing certificates, please wait...')
  })

  it('renders a success result at the zero-count boundary', async () => {
    await renderView(defaultProps({ status: 'processed', processResult }))

    expect(container.querySelector('[role="status"]')?.textContent).toContain('0 of 0 students')
  })

  it('renders the generic error banner when there are no import errors', async () => {
    await renderView(defaultProps({ status: 'error', errorMessage: 'Template not found' }))

    expect(container.textContent).toContain('There was an error issuing the certificates.')
    expect(container.textContent).toContain('Template not found')
    expect(container.querySelector('[role="alert"]')).toBeNull()
  })

  it('renders import errors with field, missing field, and missing message variants', async () => {
    await renderView(defaultProps({
      status: 'error',
      errorMessage: 'A importacao terminou com erros.',
      importErrors: [
        {
          field: 'studentEmail',
          message: 'Missing email'
        },
        {
          field: null,
          message: 'Invalid score'
        },
        {
          field: 'studentName'
        }
      ]
    }))

    const alert = container.querySelector('[role="alert"]')

    expect(alert?.textContent).toContain('studentEmail: Missing email')
    expect(alert?.textContent).toContain('Invalid score')
    expect(alert?.textContent).toContain('studentName: Unknown error')
  })
})
