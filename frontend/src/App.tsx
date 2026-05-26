import { useCertificateConfigController } from './controllers/useCertificateConfigController'
import { CertificateConfigView } from './views/CertificateConfigView'
import './App.css'

function App() {
  const controller = useCertificateConfigController()

  return (
    <CertificateConfigView
      config={controller.config}
      errorMessage={controller.errorMessage}
      importErrors={controller.importErrors}
      processResult={controller.processResult}
      status={controller.status}
      onFieldChange={controller.updateField}
      onSubmit={controller.handleSubmit}
    />
  )
}

export default App
