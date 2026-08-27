import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  CircularProgress,
  IconButton,
} from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';
import CreateIcon from '@mui/icons-material/Create';
import { useSnackbar } from 'notistack';
import { createPoll } from '../services/pollService';

const CreatePoll = () => {
  const navigate = useNavigate();
  const { enqueueSnackbar } = useSnackbar();
  const [loading, setLoading] = useState(false);
  const [question, setQuestion] = useState('');
  const [options, setOptions] = useState(['', '']);
  const [expiredAt, setExpiredAt] = useState('');
  const [errors, setErrors] = useState({});

  const addOption = () => {
    if (options.length < 10) {
      setOptions([...options, '']);
    }
  };

  const removeOption = (index) => {
    if (options.length > 2) {
      setOptions(options.filter((_, i) => i !== index));
    }
  };

  const handleOptionChange = (index, value) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const validate = () => {
    const newErrors = {};
    if (!question.trim()) newErrors.question = 'Question is required';
    const validOptions = options.filter((o) => o.trim());
    if (validOptions.length < 2) newErrors.options = 'At least 2 non-empty options are required';
    const uniqueOptions = new Set(validOptions.map((o) => o.trim().toLowerCase()));
    if (uniqueOptions.size < validOptions.length) newErrors.options = 'Options must be unique';
    if (!expiredAt) newErrors.expiredAt = 'Expiration date is required';
    else if (new Date(expiredAt) <= new Date()) newErrors.expiredAt = 'Expiration must be in the future';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      const validOptions = options.filter((o) => o.trim()).map((o) => o.trim());
      await createPoll({
        question: question.trim(),
        options: validOptions,
        expiredAt: new Date(expiredAt).toISOString().replace('Z', ''),
      });
      enqueueSnackbar('Poll created successfully!', { variant: 'success' });
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to create poll';
      enqueueSnackbar(msg, { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box className="page-container" sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'center', pt: 6 }}>
      <Container maxWidth="sm">
        <Paper elevation={0} className="glass-card" sx={{ p: 4 }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, #6C63FF, #FF6584)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mx: 'auto',
                mb: 2,
              }}
            >
              <CreateIcon sx={{ color: '#fff', fontSize: 32 }} />
            </Box>
            <Typography variant="h4" sx={{ fontWeight: 700 }}>
              Create Poll
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Ask your question and let people vote
            </Typography>
          </Box>

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Question"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              error={!!errors.question}
              helperText={errors.question}
              multiline
              rows={2}
              sx={{ mb: 3 }}
            />

            <Typography variant="subtitle2" sx={{ mb: 1, color: '#9E9EB8' }}>
              Options
            </Typography>
            {errors.options && (
              <Typography variant="caption" color="error" sx={{ mb: 1, display: 'block' }}>
                {errors.options}
              </Typography>
            )}
            {options.map((option, index) => (
              <Box key={index} sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                <TextField
                  fullWidth
                  label={`Option ${index + 1}`}
                  value={option}
                  onChange={(e) => handleOptionChange(index, e.target.value)}
                  size="small"
                />
                {options.length > 2 && (
                  <IconButton onClick={() => removeOption(index)} sx={{ color: '#FF6584' }}>
                    <RemoveCircleIcon />
                  </IconButton>
                )}
              </Box>
            ))}
            {options.length < 10 && (
              <Button
                startIcon={<AddCircleIcon />}
                onClick={addOption}
                sx={{ mb: 3, color: '#6C63FF' }}
              >
                Add Option
              </Button>
            )}

            <TextField
              fullWidth
              label="Expiration Date & Time"
              type="datetime-local"
              value={expiredAt}
              onChange={(e) => setExpiredAt(e.target.value)}
              error={!!errors.expiredAt}
              helperText={errors.expiredAt}
              sx={{ mb: 3 }}
              InputLabelProps={{ shrink: true }}
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{
                py: 1.5,
                background: 'linear-gradient(135deg, #6C63FF, #8B83FF)',
                '&:hover': { background: 'linear-gradient(135deg, #5B54E6, #7A73FF)' },
                fontSize: '1rem',
              }}
            >
              {loading ? <CircularProgress size={24} color="inherit" /> : 'Post Poll'}
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default CreatePoll;
