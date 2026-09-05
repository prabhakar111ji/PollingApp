import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  CircularProgress,
  TextField,
  Button,
  Chip,
  LinearProgress,
  Radio,
  RadioGroup,
  FormControlLabel,
  IconButton,
  Tooltip,
  Divider,
  Avatar,
} from '@mui/material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbUpOutlinedIcon from '@mui/icons-material/ThumbUpOutlined';
import CommentIcon from '@mui/icons-material/Comment';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import PersonIcon from '@mui/icons-material/Person';
import SendIcon from '@mui/icons-material/Send';
import VisibilityIcon from '@mui/icons-material/Visibility';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import { useSnackbar } from 'notistack';
import { getPollById, votePoll, toggleLike, addComment, getAiSummary } from '../services/pollService';
import { isAuthenticated } from '../utils/auth';

const PollDetails = () => {
  const { id } = useParams();
  const { enqueueSnackbar } = useSnackbar();
  const [poll, setPoll] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedOption, setSelectedOption] = useState(null);
  const [voting, setVoting] = useState(false);
  const [liking, setLiking] = useState(false);
  const [commentText, setCommentText] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [aiSummary, setAiSummary] = useState(null);
  const [aiLoading, setAiLoading] = useState(false);

  const fetchPoll = async () => {
    try {
      const res = await getPollById(id);
      setPoll(res.data);
      setSelectedOption(res.data.selectedOptionId);
    } catch (err) {
      enqueueSnackbar('Failed to load poll', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPoll();
  }, [id]);

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
      return;
    }
    if (!selectedOption) {
      enqueueSnackbar('Please select an option', { variant: 'warning' });
      return;
    }
    setVoting(true);
    try {
      await votePoll({ pollId: poll.id, optionId: selectedOption });
      await fetchPoll();
      enqueueSnackbar('Vote submitted successfully!', { variant: 'success' });
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
      return;
    }
    setLiking(true);
    try {
      await toggleLike(poll.id);
      await fetchPoll();
    } catch (err) {
      enqueueSnackbar('Failed to toggle like', { variant: 'error' });
    } finally {
      setLiking(false);
    }
  };

  const handleComment = async (e) => {
    e.preventDefault();
    if (!isAuthenticated()) {
      enqueueSnackbar('Please login to comment', { variant: 'warning' });
      return;
    }
    if (!commentText.trim()) {
      enqueueSnackbar('Comment cannot be empty', { variant: 'warning' });
      return;
    }
    setSubmittingComment(true);
    try {
      await addComment({ pollId: poll.id, content: commentText.trim() });
      setCommentText('');
      await fetchPoll();
      enqueueSnackbar('Comment added!', { variant: 'success' });
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to add comment';
      enqueueSnackbar(msg, { variant: 'error' });
    } finally {
      setSubmittingComment(false);
    }
  };

  const handleGenerateSummary = async () => {
    setAiLoading(true);
    try {
      const res = await getAiSummary(poll.id);
      setAiSummary(res.data);
    } catch (err) {
      enqueueSnackbar('Failed to generate AI summary', { variant: 'error' });
    } finally {
      setAiLoading(false);
    }
  };

  if (loading) {
    return (
      <Box className="page-container" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <CircularProgress sx={{ color: '#6C63FF' }} />
      </Box>
    );
  }

  if (!poll) {
    return (
      <Box className="page-container" sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h5" color="text.secondary">
          Poll not found
        </Typography>
      </Box>
    );
  }

  const isExpired = poll.expired;
  const hasVoted = poll.hasVoted;
  const showResults = isExpired || hasVoted;

  return (
    <Box className="page-container">
      <Container maxWidth="md">
        {/* Poll Card */}
        <Paper elevation={0} className="glass-card" sx={{ p: 4, mb: 3 }}>
          {/* Header */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
            <Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                <PersonIcon sx={{ fontSize: 18, color: '#9E9EB8' }} />
                <Typography variant="body2" color="text.secondary">
                  {poll.creatorName}
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ ml: 1 }}>
                  • {formatDate(poll.postedDate)}
                </Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 700, color: '#E8E8F0' }}>
                {poll.question}
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
              }}
            />
          </Box>

          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            {isExpired ? 'Expired' : 'Expires'}: {formatDate(poll.expiredAt)}
          </Typography>

          {/* Options / Results */}
          {showResults ? (
            <Box sx={{ mb: 3 }}>
              {poll.options.map((option) => (
                <Box key={option.id} sx={{ mb: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                    <Typography
                      variant="body1"
                      sx={{
                        fontWeight: poll.selectedOptionId === option.id ? 700 : 400,
                        color: poll.selectedOptionId === option.id ? '#6C63FF' : '#E8E8F0',
                      }}
                    >
                      {option.title}
                      {poll.selectedOptionId === option.id && ' ✓'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {option.percentage}% ({option.voteCount} votes)
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={option.percentage}
                    sx={{
                      height: 10,
                      borderRadius: 5,
                      backgroundColor: 'rgba(108, 99, 255, 0.1)',
                      '& .MuiLinearProgress-bar': {
                        borderRadius: 5,
                        background:
                          poll.selectedOptionId === option.id
                            ? 'linear-gradient(90deg, #6C63FF, #8B83FF)'
                            : 'linear-gradient(90deg, #FF6584, #FF8FA3)',
                      },
                    }}
                  />
                </Box>
              ))}
            </Box>
          ) : (
            <Box sx={{ mb: 3 }}>
              <RadioGroup
                value={selectedOption || ''}
                onChange={(e) => setSelectedOption(Number(e.target.value))}
              >
                {poll.options.map((option) => (
                  <FormControlLabel
                    key={option.id}
                    value={option.id}
                    control={<Radio sx={{ color: '#6C63FF', '&.Mui-checked': { color: '#6C63FF' } }} />}
                    label={<Typography sx={{ color: '#E8E8F0' }}>{option.title}</Typography>}
                    sx={{
                      mb: 1,
                      mx: 0,
                      p: 1.5,
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
                  py: 1.5,
                  background: 'linear-gradient(135deg, #6C63FF, #8B83FF)',
                  '&:hover': { background: 'linear-gradient(135deg, #5B54E6, #7A73FF)' },
                }}
              >
                {voting ? 'Voting...' : 'Vote'}
              </Button>
            </Box>
          )}

          {/* Stats */}
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              gap: 3,
              pt: 2,
              borderTop: '1px solid rgba(108, 99, 255, 0.1)',
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <Tooltip title={poll.hasLiked ? 'Unlike' : 'Like'}>
                <IconButton onClick={handleLike} disabled={liking} size="small">
                  {poll.hasLiked ? (
                    <ThumbUpIcon sx={{ color: '#6C63FF' }} />
                  ) : (
                    <ThumbUpOutlinedIcon sx={{ color: '#9E9EB8' }} />
                  )}
                </IconButton>
              </Tooltip>
              <Typography variant="body2" color="text.secondary">
                {poll.likesCount} likes
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <CommentIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
              <Typography variant="body2" color="text.secondary">
                {poll.commentsCount} comments
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <HowToVoteIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
              <Typography variant="body2" color="text.secondary">
                {poll.totalVoteCount} total votes
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <Tooltip title="View Count">
                <VisibilityIcon sx={{ color: '#9E9EB8', fontSize: 20 }} />
              </Tooltip>
              <Typography variant="body2" color="text.secondary">
                {poll.viewCount} views
              </Typography>
            </Box>
          </Box>
        </Paper>

        {/* AI Summary Section */}
        {(showResults || poll.commentsCount > 0) && (
          <Paper elevation={0} className="glass-card" sx={{ p: 4, mb: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: aiSummary ? 2 : 0 }}>
              <Typography variant="h6" sx={{ fontWeight: 600, color: '#E8E8F0', display: 'flex', alignItems: 'center', gap: 1 }}>
                <AutoAwesomeIcon sx={{ color: '#FF6584' }} />
                AI Summary
              </Typography>
              {!aiSummary && (
                <Button
                  variant="outlined"
                  size="small"
                  onClick={handleGenerateSummary}
                  disabled={aiLoading}
                  startIcon={aiLoading ? <CircularProgress size={16} color="inherit" /> : <AutoAwesomeIcon />}
                  sx={{
                    borderColor: '#FF6584',
                    color: '#FF6584',
                    '&:hover': {
                      borderColor: '#FF8FA3',
                      backgroundColor: 'rgba(255, 101, 132, 0.1)'
                    }
                  }}
                >
                  Generate
                </Button>
              )}
            </Box>

            {aiSummary && (
              <Box sx={{ 
                p: 2, 
                borderRadius: 2, 
                backgroundColor: 'rgba(255, 101, 132, 0.05)',
                border: '1px solid rgba(255, 101, 132, 0.1)'
              }}>
                <Typography variant="body1" sx={{ color: '#E8E8F0', lineHeight: 1.6, fontStyle: 'italic' }}>
                  "{aiSummary.summary}"
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Powered by {aiSummary.model}
                  </Typography>
                </Box>
              </Box>
            )}
          </Paper>
        )}

        {/* Comments Section */}
        <Paper elevation={0} className="glass-card" sx={{ p: 4 }}>
          <Typography variant="h6" sx={{ fontWeight: 600, mb: 3, color: '#E8E8F0' }}>
            Comments ({poll.commentsCount})
          </Typography>

          {/* Add Comment */}
          {isAuthenticated() && (
            <Box component="form" onSubmit={handleComment} sx={{ display: 'flex', gap: 2, mb: 3 }}>
              <TextField
                fullWidth
                placeholder="Write a comment..."
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
                size="small"
                inputProps={{ maxLength: 1000 }}
              />
              <Button
                type="submit"
                variant="contained"
                disabled={submittingComment || !commentText.trim()}
                sx={{
                  minWidth: 'auto',
                  px: 3,
                  background: 'linear-gradient(135deg, #6C63FF, #8B83FF)',
                  '&:hover': { background: 'linear-gradient(135deg, #5B54E6, #7A73FF)' },
                }}
              >
                {submittingComment ? <CircularProgress size={20} color="inherit" /> : <SendIcon />}
              </Button>
            </Box>
          )}

          {/* Comments List */}
          {poll.comments && poll.comments.length > 0 ? (
            poll.comments.map((comment, index) => (
              <Box key={comment.id}>
                <Box sx={{ display: 'flex', gap: 2, py: 2 }}>
                  <Avatar
                    sx={{
                      width: 36,
                      height: 36,
                      background: 'linear-gradient(135deg, #6C63FF, #FF6584)',
                      fontSize: 14,
                    }}
                  >
                    {comment.authorName?.charAt(0)?.toUpperCase()}
                  </Avatar>
                  <Box sx={{ flex: 1 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                      <Typography variant="body2" sx={{ fontWeight: 600, color: '#E8E8F0' }}>
                        {comment.authorName}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {formatDate(comment.createdAt)}
                      </Typography>
                    </Box>
                    <Typography variant="body2" sx={{ color: '#B8B8D0' }}>
                      {comment.content}
                    </Typography>
                  </Box>
                </Box>
                {index < poll.comments.length - 1 && (
                  <Divider sx={{ borderColor: 'rgba(108, 99, 255, 0.08)' }} />
                )}
              </Box>
            ))
          ) : (
            <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
              No comments yet. Be the first to comment!
            </Typography>
          )}
        </Paper>
      </Container>
    </Box>
  );
};

export default PollDetails;
