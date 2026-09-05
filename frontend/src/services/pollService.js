import api from './api';

export const createPoll = (data) => api.post('/user/poll', data);

export const getAllPolls = (page = 0, size = 10) => api.get(`/user/poll?page=${page}&size=${size}`);

export const getPollById = (id) => api.get(`/user/poll/${id}`);

export const getMyPolls = () => api.get('/user/poll/my');

export const deletePoll = (id) => api.delete(`/user/poll/${id}`);

export const votePoll = (data) => api.post('/user/poll/vote', data);

export const toggleLike = (pollId) => api.post(`/user/poll/${pollId}/like`);

export const addComment = (data) => api.post('/user/poll/comment', data);

export const getAiSummary = (pollId) => api.get(`/user/poll/${pollId}/ai-summary`);
