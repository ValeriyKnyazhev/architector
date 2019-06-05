import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';
import { notification } from 'antd';
import axios from 'axios';

import 'flexboxgrid2/flexboxgrid2.css';
import './index.css';
import App from './App';

(async () => {
  const defaultHeaders = {
    Accept: 'application/json'
  };

  axios.defaults.headers.common = defaultHeaders;

  axios.interceptors.response.use(
    function(response) {
      return response;
    },
    function(error) {
      const response = error.response;

      if (response.status >= 500) {
        notification.error({
          message: 'Server Internal Error',
          description: 'Something went wrong. Try it later, please.'
        });
      } else if (response.status >= 400) {
        notification.warning({
          message: 'Warning',
          description: response.data.message || response.data.error
        });
      }

      return Promise.reject(error);
    }
  );

  ReactDOM.render(
    <BrowserRouter>
      <App />
    </BrowserRouter>,
    document.getElementById('root')
  );
})();
