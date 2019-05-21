import React, { Component } from 'react';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Icon } from 'antd';

import './Navigation.css';
import axios from 'axios';

class Navigation extends Component {
  state = {
    architector: {
      email: ''
    }
  };

  async componentDidMount() {
    this.fetchArchitectorInfo.call(this);
  }

  async fetchArchitectorInfo() {
    const { data } = await axios.get(`/api/me`);
    this.setState({ architector: data });
  }

  render() {
    const {
      architector: { email }
    } = this.state;

    return (
      <header>
        <div className="container architector">
          <div className="row architector__header">
            <Link to="/projects">
              <h3 className="architector__header-title start-xs">Architector</h3>
            </Link>
            <div className="architector__header-user col-xs-6">User: {email}</div>
            <div className="col-xs-5 end-xs">
              <Button
                className="architector__header-logout "
                href="/logout"
                type="danger"
                style={{ alignContent: 'right' }}
              >
                Logout <Icon type="logout" />
              </Button>
            </div>
          </div>
        </div>
      </header>
    );
  }
}

export default withRouter(Navigation);
