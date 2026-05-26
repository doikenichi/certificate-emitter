import { afterEach, describe, expect, it, vi } from 'vitest'

import type { CertificateConfig } from '../models/CertificateConfig'
import { startCertificateProcess, type ProcessResponse } from './certificateApi'

const config: CertificateConfig = {
  formAnswersName: 'answers.xlsx',
  certificateTemplate: 'certificate.docx',
  emailTemplateName: 'email.html'
}

const processResponse: ProcessResponse = {
  rowsRead: 3,
  rowsImported: 2,
  rowsSkipped: 1,
  studentsCreated: 2,
  takesCreated: 2,
  responsesCreated: 4,
  responsesUpdated: 1,
  certificatesGenerated: 2,
  errors: [
    {
      field: 'studentEmail',
      message: 'Missing email'
    }
  ]
}

function jsonResponse(body: unknown, init?: ResponseInit): Response {
  return new Response(JSON.stringify(body), {
    ...init,
    headers: {
      'content-type': 'application/json',
      ...init?.headers
    }
  })
}

describe('startCertificateProcess', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('posts the certificate config and returns the parsed process response', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse(processResponse, { status: 200 }))
    vi.stubGlobal('fetch', fetchMock)

    await expect(startCertificateProcess(config)).resolves.toEqual(processResponse)

    expect(fetchMock).toHaveBeenCalledWith('/api/certificates/import', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(config)
    })
  })

  it('throws the server message for failed json error responses', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(jsonResponse({ status: 'error', message: 'Template not found' }, { status: 404 }))
    )

    await expect(startCertificateProcess(config)).rejects.toThrow('Template not found')
  })

  it.each([
    ['json error without message', jsonResponse({ status: 'error' }, { status: 400 })],
    ['plain text error', new Response('Gateway timeout', { status: 504 })],
    ['internal server error', new Response('Internal Server Error', { status: 500 })]
  ])('throws a generic message for %s', async (_caseName, response) => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response))

    await expect(startCertificateProcess(config)).rejects.toThrow('Request failed')
  })

  it('propagates fetch rejections', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))

    await expect(startCertificateProcess(config)).rejects.toThrow('Failed to fetch')
  })
})
