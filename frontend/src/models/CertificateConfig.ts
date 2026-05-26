export type CertificateConfig = {
  formAnswersName: string
  certificateTemplate: string
  emailTemplateName: string
}

export type CertificateConfigField = {
  name: keyof CertificateConfig
  label: string
}

export const defaultCertificateConfig: {
    formAnswersName: string;
    certificateTemplate: string;
    emailTemplateName: string
} = {
  formAnswersName: 'respostas_alunos',
  certificateTemplate: 'modelo.docx',
  emailTemplateName: 'template_email.html'
}

export const certificateConfigFields: CertificateConfigField[] = [
  {
    name: 'formAnswersName',
    label: 'Excel Spreadsheet with the students\' quiz answers'
  },
  {
    name: 'certificateTemplate',
    label: 'Certificate template in .docx format, with placeholders for the student name and quiz score'
  },
  {
    name: 'emailTemplateName',
    label: 'HTML document template for the email'
  }
]
