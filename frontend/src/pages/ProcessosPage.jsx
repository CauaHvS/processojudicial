import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container, Typography, Button, Box, Chip,
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, IconButton,
    TextField, MenuItem, Alert, CircularProgress
} from '@mui/material';
import { Add, Visibility, Delete, History } from '@mui/icons-material';
import { processoService } from '../services/processoService';


const statusColors = {
    EM_ANDAMENTO: 'primary',
    AGUARDANDO_PRAZO: 'warning',
    SUSPENSO: 'default',
    ENCERRADO: 'success',
};

const statusLabels = {
    EM_ANDAMENTO: 'Em Andamento',
    AGUARDANDO_PRAZO: 'Aguardando Prazo',
    SUSPENSO: 'Suspenso',
    ENCERRADO: 'Encerrado',
};

export default function ProcessosPage() {
    const navigate = useNavigate();
    const [processos, setProcessos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState(null);
    const [filtroStatus, setFiltroStatus] = useState('');

    const carregarProcessos = async () => {
        try {
            setLoading(true);
            const response = filtroStatus
                ? await processoService.listarPorStatus(filtroStatus)
                : await processoService.listarTodos();
            setProcessos(response.data);
            setErro(null);
        } catch (err) {
            setErro('Erro ao carregar processos.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        carregarProcessos();
    }, [filtroStatus]);

    const handleDeletar = async (id) => {
        if (window.confirm('Deseja realmente excluir este processo?')) {
            try {
                await processoService.deletar(id);
                carregarProcessos();
            } catch {
                setErro('Erro ao deletar processo.');
            }
        }
    };

    const isPrazoVencendo = (prazo) => {
        const hoje = new Date();
        const dataPrazo = new Date(prazo);
        const diff = (dataPrazo - hoje) / (1000 * 60 * 60 * 24);
        return diff <= 7 && diff >= 0;
    };

    return (
        <Container maxWidth="xl" sx={{ py: 4 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4" fontWeight="bold" color="primary">
                    Gestão de Processos Judiciais
                </Typography>
                <Button variant="contained" startIcon={<Add />} onClick={() => navigate('/novo')}>
                    Novo Processo
                </Button>
            </Box>

            <Box mb={2}>
                <TextField
                    select
                    label="Filtrar por Status"
                    value={filtroStatus}
                    onChange={(e) => setFiltroStatus(e.target.value)}
                    size="small"
                    sx={{ minWidth: 200 }}
                >
                    <MenuItem value="">Todos</MenuItem>
                    {Object.entries(statusLabels).map(([key, label]) => (
                        <MenuItem key={key} value={key}>{label}</MenuItem>
                    ))}
                </TextField>
            </Box>

            {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}

            {loading ? (
                <Box display="flex" justifyContent="center" mt={4}>
                    <CircularProgress />
                </Box>
            ) : (
                <TableContainer component={Paper} elevation={2}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#1976d2' }}>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Número</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Título</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Tipo</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Responsável</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Prazo</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Status</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Ações</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {processos.map((p) => (
                                <TableRow key={p.id} hover>
                                    <TableCell>{p.numero}</TableCell>
                                    <TableCell>{p.titulo}</TableCell>
                                    <TableCell>{p.tipo}</TableCell>
                                    <TableCell>{p.responsavel}</TableCell>
                                    <TableCell sx={{ color: isPrazoVencendo(p.prazo) ? 'orange' : 'inherit' }}>
                                        {new Date(p.prazo).toLocaleDateString('pt-BR')}
                                        {isPrazoVencendo(p.prazo) && ' ⚠️'}
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={statusLabels[p.status]}
                                            color={statusColors[p.status]}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <IconButton color="primary" onClick={() => navigate(`/processos/${p.id}`)}>
                                            <Visibility />
                                        </IconButton>
                                        <IconButton color="secondary" onClick={() => navigate(`/processos/${p.id}/logs`)}>
                                            <History />
                                        </IconButton>
                                        <IconButton color="error" onClick={() => handleDeletar(p.id)}>
                                            <Delete />
                                        </IconButton>

                                    </TableCell>
                                </TableRow>
                            ))}
                            {processos.length === 0 && (
                                <TableRow>
                                    <TableCell colSpan={7} align="center">
                                        Nenhum processo encontrado.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Container>
    );
}