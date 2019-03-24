import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter } from "react-router-dom";
import axios from "axios";

import "./index.css";
import App from "./App";

(async () => {
  const defaultHeaders = {
    Accept: "application/json"
  };

  axios.defaults.headers.common = defaultHeaders;

  ReactDOM.render(
    <BrowserRouter>
      <App />
    </BrowserRouter>,
    document.getElementById("root")
  );
})();