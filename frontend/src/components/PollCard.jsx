import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Radio,
  RadioGroup,
  FormControlLabel,
  Button,
  LinearProgress,
  Chip,
  IconButton,
  Tooltip,
} from '@mui/material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbUpOutlinedIcon from '@mui/icons-material/ThumbUpOutlined';
import CommentIcon from '@mui/icons-material/Comment';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import PersonIcon from '@mui/icons-material/Person';
import { useSnackbar } from 'notistack';
import { votePoll, toggleLike } from '../services/pollService';
import { isAuthenticated } from '../utils/auth';

const PollCard = ({ poll, onUpdate, onDelete, showDelete = false }) => {
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const [selectedOption, setSelectedOption] = useState(poll.selectedOptionId || null);
  const [voting, setVoting] = useState(false);
  const [liking, setLiking] = useState(false);
  const [currentPoll, setCurrentPoll] = useState(poll);

  const isExpired = currentPoll.expired;
  const hasVoted = currentPoll.hasVoted;
  const showResults = isExpired || hasVoted;

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const handleVote = async () => {
    if (!isAuthenticated()) {
      enqueueSnackbar('Please login to vote', { variant: 'warning' });
      navigate('/login');
      return;
    }
    if (!selectedOption) {
      enqueueSnackbar('Please select an option', { variant: 'warning' });
      return;
    }
    setVoting(true);
    try {
      const res = await votePoll({ pollId: currentPoll.id, optionId: selectedOption });
      setCurrentPoll(res.data);
      enqueueSnackbar('Vote submitted successfully!', { variant: 'success' });
      if (onUpdate) onUpdate(res.data);
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to vote';
      enqueueSnackbar(msg, { variant: 'error' });
    } finally {
      setVoting(false);
    }
  };

  const handleLike = async () => {
    if (!isAuthenticated()) {
      enqueueSnackbar('Please login to like', { variant: 'warning' });
      navigate('/login');
      return;
    }
    setLiking(true);
    try {
      const res = await toggleLike(currentPoll.id);
      setCurrentPoll(res.data);
      if (onUpdate) onUpdate(res.data);
    } catch (err) {
      enqueueSnackbar('Failed to toggle like', { variant: 'error' });
    } finally {
      setLiking(false);
    }
  };

  return (
    <Card
      className="glass-card"
      sx={{
        mb: 3,
        overflow: 'visible',
        transition: 'all 0.3s ease',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 12px 40px rgba(108, 99, 255, 0.2)',
        },
      }}
    >
      <CardContent sx={{ p: 3 }}>
        {/* Header */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
              <PersonIcon sx={{ fontSize: 16, color: '#9E9EB8' }} />
              <Typography variant="body2" color="text.secondary">
                {currentPoll.creatorName}
              </Typography>
            </Box>
            <Typography variant="h6" sx={{ fontWeight: 600, color: '#E8E8F0', lineHeight: 1.3 }}>
              {currentPoll.question}
            </Typography>
          </Box>
          <Chip
            icon={<AccessTimeIcon sx={{ fontSize: 14 }} />}
            label={isExpired ? 'Expired' : 'Active'}
            size="small"
            sx={{
              backgroundColor: isExpired ? 'rgba(255, 101, 132, 0.15)' : 'rgba(76, 175, 80, 0.15)',
              color: isExpired ? '#FF6584' : '#4CAF50',
              border: `1px solid ${isExpired ? 'rgba(255, 101, 132, 0.3)' : 'rgba(76, 175, 80, 0.3)'}`,
              fontWeight: 600,
              fontSize: '0.7rem',
            }}
          />
        </Box>

        {/* Expiration info */}
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
          {isExpired ? 'Expired' : 'Expires'}: {formatDate(currentPoll.expiredAt)}
        </Typography>

        {/* Options */}
        {showResults ? (
          <Box sx={{ mb: 2 }}>
            {currentPoll.options.map((option) => (
              <Box key={option.id} sx={{ mb: 1.5 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography
                    variant="body2"
                    sx={{
                      fontWeight: currentPoll.selectedOptionId === option.id ? 700 : 400,
                      color: currentPoll.selectedOptionId === option.id ? '#6C63FF' : '#E8E8F0',
                    }}
                  >
                    {option.title}
                    {currentPoll.selectedOptionId === option.id && ' ✓'}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {option.percentage}% ({option.voteCount})
                  </Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={option.percentage}
                  sx={{
                    height: 8,
                    borderRadius: 4,
                    backgroundColor: 'rgba(108, 99, 255, 0.1)',
                    '& .MuiLinearProgress-bar': {
                      borderRadius: 4,
                      background:
                        currentPoll.selectedOptionId === option.id
                          ? 'linear-gradient(90deg, #6C63FF, #8B83FF)'
                          : 'linear-gradient(90deg, #FF6584, #FF8FA3)',
                    },
                  }}
                />
              </Box>
            ))}
          </Box>
        ) : (
          <Box sx={{ mb: 2 }}>
            <RadioGroup
              value={selectedOption || ''}
              onChange={(e) => setSelectedOption(Number(e.target.value))}
            >
              {currentPoll.options.map((option) => (
                <FormControlLabel
                  key={option.id}
                  value={option.id}
                  control={
                    <Radio
                      sx={{
                        color: '#6C63FF',
                        '&.Mui-checked': { color: '#6C63FF' },
                      }}
                    />
                  }
                  label={
                    <Typography variant="body2" sx={{ color: '#E8E8F0' }}>
                      {option.title}
                    </Typography>
                  }
                  sx={{
                    mb: 0.5,
                    mx: 0,
                    p: 1,
                    borderRadius: 2,
                    border: '1px solid rgba(108, 99, 255, 0.1)',
                    '&:hover': { backgroundColor: 'rgba(108, 99, 255, 0.05)' },
                    width: '100%',
                  }}
                />
              ))}
            </RadioGroup>
            <Button
              variant="contained"
              fullWidth
              onClick={handleVote}
              disabled={voting || !selectedOption}
              startIcon={<HowToVoteIcon />}
              sx={{
                mt: 2,
                background: 'linear-gradient(135deg, #6C63FF, #8B83FF)',
                '&:hover': { background: 'linear-gradient(135deg, #5B54E6, #7A73FF)' },
                '&:disabled': { background: 'rgba(108, 99, 255, 0.3)' },
              }}
            >
              {voting ? 'Voting...' : 'Vote'}
            </Button>
          </Box>
        )}

        {/* Footer */}
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            pt: 2,
            borderTop: '1px solid rgba(108, 99, 255, 0.1)',
          }}
        >
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Tooltip title={currentPoll.hasLiked ? 'Unlike' : 'Like'}>
              <IconButton onClick={handleLike} disabled={liking} size="small">
                {currentPoll.hasLiked ? (
                  <ThumbUpIcon sx={{ color: '#6C63FF', fontSize: 20 }} />
                ) : (
                  <ThumbUpOutlinedIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
                )}
              </IconButton>
            </Tooltip>
            <Typography variant="body2" color="text.secondary">
              {currentPoll.likesCount}
            </Typography>

            <CommentIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
            <Typography variant="body2" color="text.secondary">
              {currentPoll.commentsCount}
            </Typography>

            <HowToVoteIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
            <Typography variant="body2" color="text.secondary">
              {currentPoll.totalVoteCount} votes
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', gap: 1 }}>
            <Tooltip title="View Details">
              <IconButton
                onClick={() => navigate(`/poll/${currentPoll.id}`)}
                size="small"
                sx={{ color: '#6C63FF' }}
              >
                <VisibilityIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            {showDelete && onDelete && (
              <Tooltip title="Delete Poll">
                <IconButton
                  onClick={() => onDelete(currentPoll.id)}
                  size="small"
                  sx={{ color: '#FF6584' }}
                >
                  <DeleteIcon fontSize="small" />
                </IconButton>
              </Tooltip>
            )}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default PollCard;
