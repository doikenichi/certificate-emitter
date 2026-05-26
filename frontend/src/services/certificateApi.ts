import type { CertificateConfig } from '../models/CertificateConfig'

const API_BASE = '/api/certificates'

export type ImportError = {
  field?: string | null
  message?: string | null
}

export type ProcessResponse = {
  rowsRead: number
  rowsImported: number
  rowsSkipped: number
  studentsCreated: number
  takesCreated: number
  responsesCreated: number
  responsesUpdated: number
  certificatesGenerated: number
  errors: ImportError[]
}

type ErrorResponse = {
  status: 'error'
  message: string
}

async function parseResponse<T>(response: Response): Promise<T> {
  const contentType = response.headers.get('content-type') || ''
  const body = contentType.includes('application/json')
    ? await response.json()
    : await response.text()

  if (!response.ok) {
    const message = typeof body === 'object' && body !== null && 'message' in body
      ? String((body as ErrorResponse).message)
      : 'Request failed'
    throw new Error(message)
  }

  return body as T
}

export async function startCertificateProcess(config: CertificateConfig): Promise<ProcessResponse> {
  const response = await fetch(`${API_BASE}/import`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(config)
  })

  return parseResponse<ProcessResponse>(response)
}

