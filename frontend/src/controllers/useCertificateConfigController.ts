import type { FormEvent } from 'react'
import { useState } from 'react'
import { startCertificateProcess, type ImportError, type ProcessResponse } from '../services/certificateApi'
import { type CertificateConfig, defaultCertificateConfig } from '../models/CertificateConfig'

export type Status = 'idle' | 'running' | 'processed' | 'error'

export function useCertificateConfigController() {
  const [config, setConfig] = useState<CertificateConfig>(defaultCertificateConfig)
  const [status, setStatus] = useState<Status>('idle')
  const [errorMessage, setErrorMessage] = useState('')
  const [importErrors, setImportErrors] = useState<ImportError[]>([])
  const [processResult, setProcessResult] = useState<ProcessResponse | null>(null)

  function updateField(name: keyof CertificateConfig, value: string) {
    setConfig((currentConfig) => ({
      ...currentConfig,
      [name]: value
    }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    setStatus('running')
    setErrorMessage('')
    setImportErrors([])
    setProcessResult(null)

    try {
      const result = await startCertificateProcess(config)
      setProcessResult(result)

      if (result.errors.length > 0) {
        setImportErrors(result.errors)
        setErrorMessage('A importacao terminou com erros.')
        setStatus('error')
        return
      }

      setStatus('processed')
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Unexpected error')
      setStatus('error')
    }
  }

  return {
    config,
    errorMessage,
    handleSubmit,
    importErrors,
    processResult,
    status,
    updateField
  }
}
