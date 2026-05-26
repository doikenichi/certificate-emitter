/* @vitest-environment jsdom */
import { act } from 'react'
import { createRoot, type Root } from 'react-dom/client'
import { afterEach, describe, expect, it } from 'vitest'

import App from './App'
import { defaultCertificateConfig } from './models/CertificateConfig'

;(globalThis as typeof globalThis & { IS_REACT_ACT_ENVIRONMENT: boolean }).IS_REACT_ACT_ENVIRONMENT = true

let root: Root
let container: HTMLDivElement

async function renderApp() {
  container = document.createElement('div')
  document.body.append(container)
  root = createRoot(container)

  await act(async () => {
    root.render(<App />)
  })
}

describe('App', () => {
  afterEach(() => {
    act(() => {
      root?.unmount()
    })
    container?.remove()
  })

  it('renders the certificate config workflow with default values', async () => {
    await renderApp()

    const inputs = container.querySelectorAll<HTMLInputElement>('input')

    expect(container.textContent).toContain('Issue Certificates')
    expect(inputs).toHaveLength(3)
    expect(inputs[0]?.value).toBe(defaultCertificateConfig.formAnswersName)
    expect(inputs[1]?.value).toBe(defaultCertificateConfig.certificateTemplate)
    expect(inputs[2]?.value).toBe(defaultCertificateConfig.emailTemplateName)
    expect(container.querySelector<HTMLButtonElement>('button.submit')?.disabled).toBe(false)
  })
})
