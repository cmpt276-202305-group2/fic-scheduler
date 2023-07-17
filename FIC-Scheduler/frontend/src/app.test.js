import React from 'react';
import { render, screen } from '@testing-library/react';
import { within } from '@testing-library/dom';
import { MemoryRouter } from 'react-router-dom';
import App from './App';
import LoginPage from "./pages/LoginPage";
import InstructorHomePage from "./pages/InstructorHomePage";
import CoordinatorHomePage from "./pages/CoordinatorHomePage";
import LogoutPage from "./pages/LogoutPage";

jest.mock('./pages/LoginPage', () => {
  return function DummyLoginPage() {
    return (
      <div data-testid="login">LoginPage Component</div>
    );
  };
});

jest.mock('./pages/InstructorHomePage', () => {
  return function DummyInstructorHomePage() {
    return (
      <div data-testid="instructor">InstructorHomePage Component</div>
    );
  };
});

jest.mock('./pages/CoordinatorHomePage', () => {
  return function DummyCoordinatorHomePage() {
    return (
      <div data-testid="coordinator">CoordinatorHomePage Component</div>
    );
  };
});

jest.mock('./pages/LogoutPage', () => {
  return function DummyLogoutPage() {
    return (
      <div data-testid="logout">LogoutPage Component</div>
    );
  };
});

test('full app rendering/navigating', () => {
    let { container } = render(<MemoryRouter initialEntries={['/login']}><App /></MemoryRouter>);
    let getByTestId = within(container).getByTestId;
    expect(getByTestId('login')).toBeInTheDocument();
  
    ({ container } = render(<MemoryRouter initialEntries={['/InstructorHomePage']}><App /></MemoryRouter>));
    getByTestId = within(container).getByTestId;
    // expect(getByTestId('instructor')).toBeInTheDocument();
  
    ({ container } = render(<MemoryRouter initialEntries={['/CoordinatorHomePage']}><App /></MemoryRouter>));
    getByTestId = within(container).getByTestId;
    // expect(getByTestId('coordinator')).toBeInTheDocument();
  
    ({ container } = render(<MemoryRouter initialEntries={['/LogoutPage']}><App /></MemoryRouter>));
    getByTestId = within(container).getByTestId;
    // expect(getByTestId('logout')).toBeInTheDocument();
  });
  
