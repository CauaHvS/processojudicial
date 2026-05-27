import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container, Typography, Button, Box,
    TextField, MenuItem, Alert, Paper
} from '@mui/material';
import { ArrowBack, Save } from '@mui/icons-material';
import { processoService } from '../services/processoService';

const statusOptions = [
    { value: 'EM_ANDAMENTO', label: 'Em Andamento' },
    { value: 'AGUARDANDO_PRAZO', label: 'Aguardando Prazo' },
    { value: 'SUSPENSO', label: 'Suspenso' },
    { value: 'ENCERRADO', label: 'Encerrado' },
];

export default function NovoProcessoPage() {
    const navigate = useNavigate();
    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(false);
    const [form, setForm] = useState({
        numero: '',
        titulo: '',
        tipo: '',
        descricao: '',
        status: 'EM_ANDAMENTO',
        responsavel: '',
        prazo: '',
    });

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await processoService.criar(form);
            setSucesso(true);
            setTimeout(() => navigate('/'), 1500);
        } catch (err) {
            setErro(err.response?.data?.erro || 'Erro ao criar processo.');
        }
    };

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <Box display="flex" alignItems="center" mb={3} gap={2}>
                <Button startIcon={<ArrowBack />} onClick={() => navigate('/')}>
                    Voltar
                </Button>
                <Typography variant="h5" fontWeight="bold" color="primary">
                    Novo Processo
                </Typography>
            </Box>

            <Paper elevation={2} sx={{ p: 4 }}>
                {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}
                {sucesso && <Alert severity="success" sx={{ mb: 2 }}>Processo criado com sucesso!</Alert>}

                <Box component="form" onSubmit={handleSubmit} display="flex" flexDirection="column" gap={2}>
                    <Box display="flex" gap={2}>
                        <TextField
                            label="Número do Processo"
                            name="numero"
                            value={form.numero}
                            onChange={handleChange}
                            required
                            fullWidth
                        />
                        <TextField
                            label="Tipo"
                            name="tipo"
                            value={form.tipo}
                            onChange={handleChange}
                            required
                            fullWidth
                        />
                    </Box>

                    <TextField
                        label="Título"
                        name="titulo"
                        value={form.titulo}
                        onChange={handleChange}
                        required
                        fullWidth
                    />

                    <TextField
                        label="Descrição"
                        name="descricao"
                        value={form.descricao}
                        onChange={handleChange}
                        multiline
                        rows={3}
                        fullWidth
                    />

                    <Box display="flex" gap={2}>
                        <TextField
                            label="Responsável"
                            name="responsavel"
                            value={form.responsavel}
                            onChange={handleChange}
                            required
                            fullWidth
                        />
                        <TextField
                            select
                            label="Status"
                            name="status"
                            value={form.status}
                            onChange={handleChange}
                            required
                            fullWidth
                        >
                            {statusOptions.map((op) => (
                                <MenuItem key={op.value} value={op.value}>{op.label}</MenuItem>
                            ))}
                        </TextField>
                    </Box>

                    <TextField
                        label="Prazo"
                        name="prazo"
                        type="date"
                        value={form.prazo}
                        onChange={handleChange}
                        required
                        fullWidth
                        InputLabelProps={{ shrink: true }}
                    />

                    <Box display="flex" justifyContent="flex-end" mt={2}>
                        <Button
                            type="submit"
                            variant="contained"
                            startIcon={<Save />}
                            size="large"
                        >
                            Salvar Processo
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
}