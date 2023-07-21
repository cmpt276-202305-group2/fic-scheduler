// CheckAuth.js
import React from "react";
import { useContext } from 'react';
import { UserInfoContext } from '../App';

function CheckAuth({ children, permittedRoles, fallback }) {
  const { userInfo } = useContext(UserInfoContext);

  if ((userInfo ?? null) !== null) {
    for (const role of (userInfo.roles ?? [])) {
      if (permittedRoles.includes(role)) {
        return children;
      }
    }
  }
  return fallback ?? (<React.Fragment />);
}

export default CheckAuth;
