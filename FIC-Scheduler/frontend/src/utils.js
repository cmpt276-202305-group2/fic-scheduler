import jwtDecode from 'jwt-decode';

export function parseJwtToken(token) {
  try {
    return jwtDecode(token);
  } catch (error) {
    // console.error('Invalid token specified', error);
    return null;
  }
}

export function isJwtTokenExpired(token) {
  try {
    const { exp } = parseJwtToken(token);

    if (!exp) {
      return true;
    }

    const currentTime = new Date().getTime() / 1000;

    return currentTime > exp;
  } catch (err) {
    return true;
  }
}

export function tokenConfig() {
  const token = localStorage.getItem('jwtToken');
  if ((token ?? null) !== null) {
    return {
      headers: {
        Authorization: "Bearer " + localStorage.getItem('jwtToken')
      }
    };
  } else {
    return {};
  }

}

