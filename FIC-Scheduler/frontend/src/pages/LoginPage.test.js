// import React from 'react';
// import { render, screen, fireEvent, waitFor } from '@testing-library/react';
// import { BrowserRouter as Router } from 'react-router-dom';
// import axios from 'axios';

// import LoginPage from './LoginPage';

// jest.mock('axios');

// test('login successfully', async () => {
//   axios.post.mockResolvedValue({
//     data: {
//       pub: 'adminUser',
//       roles: ['ADMIN'],
//     },
//     status: 200,
//   });

//   render(
//     <Router>
//       <LoginPage />
//     </Router>
//   );

//   const usernameInput = screen.getByPlaceholderText('Enter your username');
//   const passwordInput = screen.getByPlaceholderText('Enter your password');
//   const loginButton = screen.getByText('Login');

//   fireEvent.change(usernameInput, { target: { value: 'adminUser' } });
//   fireEvent.change(passwordInput, { target: { value: 'adminPass' } });
//   fireEvent.click(loginButton);

//   await waitFor(() => {
//     expect(axios.post).toHaveBeenCalledWith('http://localhost:8080/auth/login', {
//       username: 'adminUser',
//       password: 'adminPass',
//     });
//   });
// });
