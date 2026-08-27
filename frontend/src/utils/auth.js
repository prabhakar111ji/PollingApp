import Cookies from 'js-cookie';

const TOKEN_KEY = 'poll_app_token';
const USER_KEY = 'poll_app_user';

export const setToken = (token) => {
  Cookies.set(TOKEN_KEY, token, { expires: 1, sameSite: 'Lax' });
};

export const getToken = () => {
  return Cookies.get(TOKEN_KEY) || null;
};

export const removeToken = () => {
  Cookies.remove(TOKEN_KEY);
};

export const setUser = (user) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
};

export const getUser = () => {
  const user = localStorage.getItem(USER_KEY);
  return user ? JSON.parse(user) : null;
};

export const removeUser = () => {
  localStorage.removeItem(USER_KEY);
};

export const isAuthenticated = () => {
  return !!getToken();
};

export const logout = () => {
  removeToken();
  removeUser();
};
