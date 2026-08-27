import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  useMediaQuery,
  useTheme,
  Menu,
  MenuItem,
} from '@mui/material';
import PollIcon from '@mui/icons-material/Poll';
import MenuIcon from '@mui/icons-material/Menu';
import { isAuthenticated, logout, getUser } from '../utils/auth';

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [anchorEl, setAnchorEl] = React.useState(null);

  const loggedIn = isAuthenticated();
  const user = getUser();

  const handleLogout = () => {
    logout();
    setAnchorEl(null);
    navigate('/login');
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const navTo = (path) => {
    setAnchorEl(null);
    navigate(path);
  };

  const isActive = (path) => location.pathname === path;

  const navButtonStyle = (path) => ({
    color: isActive(path) ? '#6C63FF' : '#E8E8F0',
    fontWeight: isActive(path) ? 700 : 500,
    borderBottom: isActive(path) ? '2px solid #6C63FF' : '2px solid transparent',
    borderRadius: 0,
    px: 2,
    '&:hover': {
      backgroundColor: 'rgba(108, 99, 255, 0.08)',
    },
  });

  return (
    <AppBar
      position="sticky"
      sx={{
        background: 'rgba(10, 14, 26, 0.85)',
        backdropFilter: 'blur(20px)',
        borderBottom: '1px solid rgba(108, 99, 255, 0.15)',
        boxShadow: '0 4px 30px rgba(0, 0, 0, 0.3)',
      }}
    >
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        <Box
          component={Link}
          to={loggedIn ? '/dashboard' : '/login'}
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 1,
            textDecoration: 'none',
          }}
        >
          <PollIcon sx={{ color: '#6C63FF', fontSize: 32 }} />
          <Typography
            variant="h6"
            sx={{
              background: 'linear-gradient(135deg, #6C63FF, #FF6584)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              fontWeight: 700,
              letterSpacing: '-0.5px',
            }}
          >
            Polling App
          </Typography>
        </Box>

        {isMobile ? (
          <>
            <IconButton color="inherit" onClick={handleMenuOpen}>
              <MenuIcon />
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              PaperProps={{
                sx: {
                  background: '#131833',
                  border: '1px solid rgba(108, 99, 255, 0.15)',
                },
              }}
            >
              {loggedIn ? (
                [
                  <MenuItem key="dash" onClick={() => navTo('/dashboard')}>Dashboard</MenuItem>,
                  <MenuItem key="create" onClick={() => navTo('/create-poll')}>Create Poll</MenuItem>,
                  <MenuItem key="my" onClick={() => navTo('/my-polls')}>My Polls</MenuItem>,
                  <MenuItem key="logout" onClick={handleLogout}>Logout</MenuItem>,
                ]
              ) : (
                [
                  <MenuItem key="login" onClick={() => navTo('/login')}>Login</MenuItem>,
                  <MenuItem key="register" onClick={() => navTo('/register')}>Register</MenuItem>,
                ]
              )}
            </Menu>
          </>
        ) : (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            {loggedIn ? (
              <>
                <Button sx={navButtonStyle('/dashboard')} onClick={() => navigate('/dashboard')}>
                  Dashboard
                </Button>
                <Button sx={navButtonStyle('/create-poll')} onClick={() => navigate('/create-poll')}>
                  Create Poll
                </Button>
                <Button sx={navButtonStyle('/my-polls')} onClick={() => navigate('/my-polls')}>
                  My Polls
                </Button>
                {user && (
                  <Typography variant="body2" sx={{ color: '#9E9EB8', mx: 1 }}>
                    Hi, {user.firstName}
                  </Typography>
                )}
                <Button
                  variant="outlined"
                  color="secondary"
                  onClick={handleLogout}
                  sx={{ ml: 1, borderRadius: 8 }}
                >
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button sx={navButtonStyle('/login')} onClick={() => navigate('/login')}>
                  Login
                </Button>
                <Button
                  variant="contained"
                  onClick={() => navigate('/register')}
                  sx={{
                    ml: 1,
                    background: 'linear-gradient(135deg, #6C63FF, #8B83FF)',
                    '&:hover': { background: 'linear-gradient(135deg, #5B54E6, #7A73FF)' },
                  }}
                >
                  Register
                </Button>
              </>
            )}
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
