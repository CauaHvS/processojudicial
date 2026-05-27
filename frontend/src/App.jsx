import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProcessosPage from './pages/ProcessosPage';
import NovoProcessoPage from './pages/NovoProcessoPage';
import DetalhesProcessoPage from './pages/DetalhesProcessoPage';
import LogsProcessoPage from './pages/LogsProcessoPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ProcessosPage />} />
        <Route path="/novo" element={<NovoProcessoPage />} />
        <Route path="/processos/:id" element={<DetalhesProcessoPage />} />
        <Route path="/processos/:id/logs" element={<LogsProcessoPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;