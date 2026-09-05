import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, CircularProgress, Button } from '@mui/material';
import PollIcon from '@mui/icons-material/Poll';
import PollCard from '../components/PollCard';
import { getAllPolls } from '../services/pollService';
import { useSnackbar } from 'notistack';

const Dashboard = () => {
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loadingMore, setLoadingMore] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const fetchPolls = async (pageNum = 0, isLoadMore = false) => {
    if (isLoadMore) setLoadingMore(true);
    else setLoading(true);

    try {
      const res = await getAllPolls(pageNum, 10);
      const newPolls = res.data.content || res.data; // Handle both paginated and unpaginated responses just in case
      setTotalPages(res.data.totalPages || 1);
      
      if (isLoadMore) {
        setPolls((prev) => [...prev, ...newPolls]);
      } else {
        setPolls(newPolls);
      }
    } catch (err) {
      enqueueSnackbar('Failed to load polls', { variant: 'error' });
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  useEffect(() => {
    fetchPolls(0, false);
  }, []);

  const handleLoadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchPolls(nextPage, true);
  };

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
          <>
            {polls.map((poll) => (
              <PollCard key={poll.id} poll={poll} onUpdate={handlePollUpdate} />
            ))}
            
            {page < totalPages - 1 && (
              <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4, mb: 4 }}>
                <Button 
                  variant="outlined" 
                  onClick={handleLoadMore} 
                  disabled={loadingMore}
                  sx={{ 
                    borderColor: '#6C63FF', 
                    color: '#6C63FF',
                    borderRadius: '20px',
                    px: 4,
                    '&:hover': {
                      borderColor: '#5B54E6',
                      backgroundColor: 'rgba(108, 99, 255, 0.04)'
                    }
                  }}
                >
                  {loadingMore ? <CircularProgress size={24} sx={{ color: '#6C63FF' }} /> : 'Load More Polls'}
                </Button>
              </Box>
            )}
          </>
        )}
      </Container>
    </Box>
  );
};

export default Dashboard;
