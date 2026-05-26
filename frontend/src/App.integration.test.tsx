import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'

import App from './App'
import { startCertificateProcess } from './services/certificateApi'

vi.mock('./services/certificateApi', () => ({
    startCertificateProcess: vi.fn()
}))

const startCertificateProcessMock = vi.mocked(startCertificateProcess)

describe('App certificate workflow', () => {
    it('submits certificate config and shows the success result', async () => {
        startCertificateProcessMock.mockResolvedValue({
            rowsRead: 2,
            rowsImported: 2,
            rowsSkipped: 0,
            studentsCreated: 2,
            takesCreated: 2,
            responsesCreated: 6,
            responsesUpdated: 0,
            certificatesGenerated: 2,
            errors: []
        })

        const user = userEvent.setup()
        render(<App />)

        await user.clear(screen.getByDisplayValue('respostas_alunos'))
        await user.type(screen.getByLabelText(/Excel Spreadsheet/i), 'answers.xlsx')

        await user.click(screen.getByRole('button', { name: /Start issuing certificates/i }))

        expect(startCertificateProcessMock).toHaveBeenCalledWith({
            formAnswersName: 'answers.xlsx',
            certificateTemplate: 'modelo.docx',
            emailTemplateName: 'template_email.html'
        })

        expect(await screen.findByText(/Successfully issued all certificates/i)).toBeInTheDocument()
        expect(screen.getByText(/2 of 2 students/i)).toBeInTheDocument()
    })
})