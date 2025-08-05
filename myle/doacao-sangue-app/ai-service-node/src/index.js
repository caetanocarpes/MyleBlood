require('dotenv').config()
const express = require('express')
const cors = require('cors')

const app = express()
app.use(cors())
app.use(express.json())

// Exemplo de rota de IA
app.post('/api/ia/pergunta', async (req, res) => {
  const { pergunta } = req.body

  // Simulação de resposta inteligente (substituir com OpenAI ou outro)
  const resposta = `Recebi sua dúvida: "${pergunta}". Em breve teremos uma IA real aqui.`

  res.json({ resposta })
})

const PORT = process.env.PORT || 4000
app.listen(PORT, () => {
  console.log(`🧠 IA rodando na porta ${PORT}`)
})
