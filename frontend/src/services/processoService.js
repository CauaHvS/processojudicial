import api from './api';

export const processoService = {
    listarTodos: () => api.get('/processos'),
    buscarPorId: (id) => api.get(`/processos/${id}`),
    ListarPorStatus: (status) => api.get(`/processos/status/${status}`),
    ListarPrazoVencendo: () => api.get('/processos/prazo-vencendo'),
    criar: (data) => api.post('processo', data),
    atualizar: (id, data) => api.put(`/processos/${id}`, data),
    atualizarStatus: (id, data) => api.patch(`/processos/${id}/status`, data),
    deletar: (id) => api.delete(`/processos/${id}`),
};
