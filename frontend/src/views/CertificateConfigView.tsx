import type { FormEvent } from 'react'
import {
  certificateConfigFields,
  type CertificateConfig
} from '../models/CertificateConfig'
import type { Status } from '../controllers/useCertificateConfigController'
import type { ImportError, ProcessResponse } from '../services/certificateApi'

type CertificateConfigViewProps = {
  config: CertificateConfig
  errorMessage: string
  importErrors: ImportError[]
  processResult: ProcessResponse | null
  status: Status
  onFieldChange: (name: keyof CertificateConfig, value: string) => void
  onSubmit: (event: FormEvent<HTMLFormElement>) => void
}

export function CertificateConfigView({
  config,
  errorMessage,
  importErrors,
  processResult,
  status,
  onFieldChange,
  onSubmit
}: CertificateConfigViewProps) {
  return (
    <main className="page">
      <section className="panel">
        <div className="tabs">
          <button className="tab active" type="button">Issue Certificates</button>
        </div>

        {status === 'error' && (
          <div className="error">
            <strong>There was an error issuing the certificates.</strong>
            <span>{errorMessage}</span>
          </div>
        )}

        <form onSubmit={onSubmit}>
          {certificateConfigFields.map((field) => (
            <label className="field" key={field.name}>
              <span>{field.label}</span>
              <input
                type="text"
                value={config[field.name] || ''}
                onChange={(event) => onFieldChange(field.name, event.target.value)}
              />
            </label>
          ))}

          <button className="submit" type="submit" disabled={status === 'running'}>
            {status === 'running' ? 'Issuing certificates, please wait...' : 'Start issuing certificates'}
          </button>
        </form>

        {status === 'processed' && processResult && (
          <div className="result success" role="status">
            <strong>Successfully issued all certificates.</strong>
            <span>
              {processResult.certificatesGenerated} of {processResult.rowsRead} students were approved and had their certificates issued.
            </span>
          </div>
        )}

        {status === 'error' && importErrors.length > 0 && (
          <div className="result error" role="alert">
            <strong>There was an error issuing the certificates.</strong>
            <ul>
              {importErrors.map((error, index) => (
                <li key={`${error.field || 'error'}-${index}`}>
                  {formatImportError(error)}
                </li>
              ))}
            </ul>
          </div>
        )}
      </section>
    </main>
  )
}

function formatImportError(error: ImportError) {
  const message = error.message || 'Unknown error'
  return error.field ? `${error.field}: ${message}` : message
}
