import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
    Container, Typography, Button, Box, Chip,
    Paper, Alert, CircularProgress, Table,
    TableBody, TableCell, TableContainer,
    TableHead, TableRow
} from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import { processoService } from '../services/processoService';

const statusLabels = {
    EM_ANDAMENTO: 'Em Andamento',
    AGUARDANDO_PRAZO: 'Aguardando Prazo',
    SUSPENSO: 'Suspenso',
    ENCERRADO: 'Encerrado',
};

const statusColors = {
    EM_ANDAMENTO: 'primary',
    AGUARDANDO_PRAZO: 'warning',
    SUSPENSO: 'default',
    ENCERRADO: 'success',
};

export default function LogsProcessoPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState(null);

    useEffect(() => {
        const carregarLogs = async () => {
            try {
                const response = await processoService.buscarLogs(id);
                setLogs(response.data);
            } catch {
                setErro('Erro ao carregar logs.');
            } finally {
                setLoading(false);
            }
        };
        carregarLogs();
    }, [id]);

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box display="flex" alignItems="center" mb={3} gap={2}>
                <Button startIcon={<ArrowBack />} onClick={() => navigate(-1)}>
                    Voltar
                </Button>
                <Typography variant="h5" fontWeight="bold" color="primary">
                    Histórico de Alterações
                </Typography>
            </Box>

            {erro && <Alert severity="error" sx={{ mb: 2 }}>{erro}</Alert>}

            {loading ? (
                <Box display="flex" justifyContent="center" mt={4}>
                    <CircularProgress />
                </Box>
            ) : (
                <Paper elevation={2}>
                    <TableContainer>
                        <Table>
                            <TableHead>
                                <TableRow sx={{ backgroundColor: '#1976d2' }}>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Data/Hora</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Usuário</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Status Anterior</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Novo Status</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Observação</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {logs.map((log) => (
                                    <TableRow key={log.id} hover>
                                        <TableCell>
                                            {new Date(log.realizadoEm).toLocaleString('pt-BR')}
                                        </TableCell>
                                        <TableCell>{log.usuario || '—'}</TableCell>
                                        <TableCell>
                                            {log.statusAnterior ? (
                                                <Chip
                                                    label={statusLabels[log.statusAnterior]}
                                                    color={statusColors[log.statusAnterior]}
                                                    size="small"
                                                />
                                            ) : '—'}
                                        </TableCell>
                                        <TableCell>
                                            {log.statusNovo ? (
                                                <Chip
                                                    label={statusLabels[log.statusNovo]}
                                                    color={statusColors[log.statusNovo]}
                                                    size="small"
                                                />
                                            ) : '—'}
                                        </TableCell>
                                        <TableCell>{log.observacao || '—'}</TableCell>
                                    </TableRow>
                                ))}
                                {logs.length === 0 && (
                                    <TableRow>
                                        <TableCell colSpan={5} align="center">
                                            Nenhum log encontrado.
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
            )}
        </Container>
    );
}