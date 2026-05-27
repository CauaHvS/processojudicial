import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Container, Typography, Button, Box, Chip,
    Paper, Grid, TextField, MenuItem, Alert,
    CircularProgress, Divider, List, ListItem,
    ListItemText
} from '@mui/material';
import { ArrowBack, Save } from '@mui/icons-material';
import { processoService } from '../services/processoService';

const statusOptions = [
    { value: 'EM_ANDAMENTO', label: 'Em Andamento' },
    { value: 'AGUARDANDO_PRAZO', label: 'Aguardando Prazo' },
    { value: 'SUSPENSO', label: 'Suspenso' },
    { value: 'ENCERRADO', label: 'Encerrado' },
];

const statusColors = {
    EM_ANDAMENTO: 'primary',
    AGUARDANDO_PRAZO: 'warning',
    SUSPENSO: 'default',
    ENCERRADO: 'success',
};

export default function DetalhesProcessoPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [processo, setProcesso] = useState(null);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState(null);
    const [sucesso, setSucesso] = useState(null);
    const [novoStatus, setNovoStatus] = useState('');
    const [observacao, setObservacao] = useState('');
    const [usuario, setUsuario] = useState(localStorage.getItem('usuario') || '');

    const carregarProcesso = async () => {
        try {
            if (!processo) setLoading(true);
            const response = await processoService.buscarPorId(id);
            setProcesso(response.data);
            setNovoStatus(response.data.status);
        } catch {
            setErro('Erro ao carregar processo.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        carregarProcesso();
    }, [id]);

    const handleAtualizarStatus = async () => {
        try {
            await processoService.atualizarStatus(id, {
                status: novoStatus,
                observacao,
                usuario,
            });
            setSucesso('Status atualizado com sucesso!');
            setObservacao('');
            carregarProcesso();
            setTimeout(() => setSucesso(null), 3000);
        } catch {
            setErro('Erro ao atualizar status.');
        }
    };

    if (loading) return (
        <Box display="flex" justifyContent="center" mt={8}>
            <CircularProgress />
        </Box>
    );

    if (!processo) return null;

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <Box display="flex" alignItems="center" mb={3} gap={2}>
                <Button startIcon={<ArrowBack />} onClick={() => navigate('/')}>
                    Voltar
                </Button>
                <Typography variant="h5" fontWeight="bold" color="primary">
                    Detalhes do Processo
                </Typography>
                <Chip
                    label={statusOptions.find(s => s.value === processo.status)?.label}
                    color={statusColors[processo.status]}
                    sx={{ ml: 2 }}
                />
            </Box>

            {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}
            {sucesso && <Alert severity="success" sx={{ mb: 2 }}>{sucesso}</Alert>}

            <Paper elevation={2} sx={{ p: 4, mb: 3, mt: 2 }}>
                <Typography variant="h6" fontWeight="bold" mb={2}>
                    Informações do Processo
                </Typography>
                <Grid container spacing={2}>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Número</Typography>
                        <Typography variant="body1" fontWeight="bold">{processo.numero}</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Tipo</Typography>
                        <Typography variant="body1">{processo.tipo}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="body2" color="text.secondary">Título</Typography>
                        <Typography variant="body1">{processo.titulo}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="body2" color="text.secondary">Descrição</Typography>
                        <Typography variant="body1">{processo.descricao || '—'}</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Responsável</Typography>
                        <Typography variant="body1">{processo.responsavel}</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Prazo</Typography>
                        <Typography variant="body1">
                            {new Date(processo.prazo).toLocaleDateString('pt-BR')}
                        </Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Criado em</Typography>
                        <Typography variant="body1">
                            {new Date(processo.criadoEm).toLocaleString('pt-BR')}
                        </Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Atualizado em</Typography>
                        <Typography variant="body1">
                            {new Date(processo.atualizadoEm).toLocaleString('pt-BR')}
                        </Typography>
                    </Grid>
                </Grid>
            </Paper>

            <Paper elevation={2} sx={{ p: 4 }}>
                <Typography variant="h6" fontWeight="bold" mb={2}>
                    Atualizar Status
                </Typography>
                <Box display="flex" flexDirection="column" gap={2}>
                    <TextField
                        select
                        label="Novo Status"
                        value={novoStatus}
                        onChange={(e) => setNovoStatus(e.target.value)}
                        fullWidth
                        sx={{ mb: 1 }}
                    >
                        {statusOptions.map((op) => (
                            <MenuItem key={op.value} value={op.value}>{op.label}</MenuItem>
                        ))}
                    </TextField>
                    <TextField
                        label="Usuário"
                        value={usuario}
                        onChange={(e) => {
                            setUsuario(e.target.value);
                            localStorage.setItem('usuario', e.target.value);
                        }}
                        fullWidth
                        sx={{ mb: 1 }}
                    />
                    <TextField
                        label="Observação"
                        value={observacao}
                        onChange={(e) => setObservacao(e.target.value)}
                        multiline
                        rows={2}
                        fullWidth
                        sx={{ mb: 1 }}
                    />
                    <Box display="flex" justifyContent="flex-end">
                        <Button
                            variant="contained"
                            startIcon={<Save />}
                            onClick={handleAtualizarStatus}
                        >
                            Atualizar Status
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
}