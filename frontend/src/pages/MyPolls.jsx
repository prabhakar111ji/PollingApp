import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';
import ListAltIcon from '@mui/icons-material/ListAlt';
import PollCard from '../components/PollCard';
import { getMyPolls, deletePoll } from '../services/pollService';
import { useSnackbar } from 'notistack';

const MyPolls = () => {
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [pollToDelete, setPollToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const { enqueueSnackbar } = useSnackbar();

  const fetchMyPolls = async () => {
    try {
      const res = await getMyPolls();
      setPolls(res.data);
    } catch (err) {
      enqueueSnackbar('Failed to load your polls', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyPolls();
  }, []);

  const handleDeleteClick = (pollId) => {
    setPollToDelete(pollId);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    setDeleting(true);
    try {
      await deletePoll(pollToDelete);
      setPolls((prev) => prev.filter((p) => p.id !== pollToDelete));
      enqueueSnackbar('Poll deleted successfully', { variant: 'success' });
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete poll';
      enqueueSnackbar(msg, { variant: 'error' });
    } finally {
      setDeleting(false);
      setDeleteDialogOpen(false);
      setPollToDelete(null);
    }
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
            <ListAltIcon sx={{ color: '#6C63FF', fontSize: 36 }} />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 700,
                background: 'linear-gradient(135deg, #6C63FF, #FF6584)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
              }}
            >
              My Polls
            </Typography>
          </Box>
          <Typography variant="body1" color="text.secondary">
            Manage the polls you&apos;ve created
          </Typography>
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress sx={{ color: '#6C63FF' }} />
          </Box>
        ) : polls.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <ListAltIcon sx={{ fontSize: 64, color: '#9E9EB8', mb: 2 }} />
            <Typography variant="h6" color="text.secondary">
              You haven&apos;t created any polls yet.
            </Typography>
          </Box>
        ) : (
          polls.map((poll) => (
            <PollCard
              key={poll.id}
              poll={poll}
              onUpdate={handlePollUpdate}
              onDelete={handleDeleteClick}
              showDelete={true}
            />
          ))
        )}

        <Dialog
          open={deleteDialogOpen}
          onClose={() => setDeleteDialogOpen(false)}
          PaperProps={{
            sx: {
              background: '#131833',
              border: '1px solid rgba(108, 99, 255, 0.2)',
              borderRadius: 3,
            },
          }}
        >
          <DialogTitle sx={{ color: '#E8E8F0' }}>Delete Poll</DialogTitle>
          <DialogContent>
            <DialogContentText sx={{ color: '#9E9EB8' }}>
              Are you sure you want to delete this poll? This action cannot be undone.
              All votes, comments, and likes will be permanently removed.
            </DialogContentText>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button
              onClick={() => setDeleteDialogOpen(false)}
              disabled={deleting}
              sx={{ color: '#9E9EB8' }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleDeleteConfirm}
              disabled={deleting}
              variant="contained"
              sx={{
                backgroundColor: '#FF6584',
                '&:hover': { backgroundColor: '#CC516A' },
              }}
            >
              {deleting ? <CircularProgress size={20} color="inherit" /> : 'Delete'}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
};

export default MyPolls;
