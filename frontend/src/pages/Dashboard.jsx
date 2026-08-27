import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, CircularProgress } from '@mui/material';
import PollIcon from '@mui/icons-material/Poll';
import PollCard from '../components/PollCard';
import { getAllPolls } from '../services/pollService';
import { useSnackbar } from 'notistack';

const Dashboard = () => {
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const { enqueueSnackbar } = useSnackbar();

  const fetchPolls = async () => {
    try {
      const res = await getAllPolls();
      setPolls(res.data);
    } catch (err) {
      enqueueSnackbar('Failed to load polls', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPolls();
  }, []);

  const handlePollUpdate = (updatedPoll) => {
    setPolls((prev) =>
      prev.map((p) => (p.id === updatedPoll.id ? updatedPoll : p))
    );
  };

  return (
    <Box className="page-container">
      <Container maxWidth="md">
        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mb: 1 }}>
            <PollIcon sx={{ color: '#6C63FF', fontSize: 36 }} />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 700,
                background: 'linear-gradient(135deg, #6C63FF, #FF6584)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
              }}
            >
              Dashboard
            </Typography>
          </Box>
          <Typography variant="body1" color="text.secondary">
            Browse and vote on the latest polls
          </Typography>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#6C63FF' }} />
          </Box>
        ) : polls.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <PollIcon sx={{ fontSize: 64, color: '#9E9EB8', mb: 2 }} />
            <Typography variant="h6" color="text.secondary">
              No polls yet. Be the first to create one!
            </Typography>
          </Box>
        ) : (
          polls.map((poll) => (
            <PollCard key={poll.id} poll={poll} onUpdate={handlePollUpdate} />
          ))
        )}
      </Container>
    </Box>
  );
};

export default Dashboard;
