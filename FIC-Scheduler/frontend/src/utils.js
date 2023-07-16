import axios from 'axios';
import jwtDecode from 'jwt-decode';

export function parseJwtToken(token) {
  try {
    return jwtDecode(token);
  } catch (error) {
    console.error('Invalid token specified', error);
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

export function getAsync(url, setResult) {
  axios.get(url, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("jwtToken")
    }
  }).then(
    (r) => {
      setResult(r.data);
    },
    (e) => {
      console.log('fetch fail:', e);
      setResult(undefined);
    }
  );
}

export async function postAsync(url, setResult) {
  axios.post(url, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("jwtToken")
    }
  }).then(
    (r) => {
      setResult(r.data);
    },
    (e) => {
      console.log('fetch fail:', e);
      setResult(undefined);
    }
  );
}

export function getSuspense(url) {
  let status = "pending";
  let result;
  let suspender = getAsync(url).then(
    (r) => {
      status = "success";
      result = r.data;
    },
    (e) => {
      status = "error";
      result = e;
    }
  );
  return {
    read() {
      if (status === "pending") {
        throw suspender;
      } else if (status === "error") {
        throw result;
      } else if (status === "success") {
        return result;
      }
    },
  };
}

