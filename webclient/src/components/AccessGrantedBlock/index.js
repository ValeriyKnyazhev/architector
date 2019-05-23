import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router';
import { Button, Icon, message, Select } from 'antd';
import './AccessGrantedBlock.css';
import axios from 'axios';

const Option = Select.Option;

class AccessGrantedBlock extends Component {
  state = {
    accessRightsType: 'READ',
    selectedUser: '',
    users: []
  };

  handleAccessRightsTypeChange = value => {
    console.log('type', value);
    this.setState({
      accessRightsType: value
    });
  };

  handleSearchUser = value => {
    this.fetchUsers(value);
  };

  async fetchUsers(query) {
    const { data } = await axios.get(`/api/architectors?query=${query}`);
    this.setState({ users: data.architectors });
  }

  handleChangeUser = value => {
    this.setState({ selectedUser: value, users: [] });
  };

  handleAddAccessRights = () => {
    const { accessRightsType, selectedUser } = this.state;
    const { projectId } = this.props;
    axios
      .post(`/api/projects/${projectId}/access-rights`, {
        email: selectedUser,
        accessRights: accessRightsType
      })
      .then(() => {
        this.props.fetchProjectInfo();
        message.success('Access rights granted');
      });
  };

  handleTakeAwayAccessRights = user => {
    const { projectId } = this.props;
    axios
      .delete(`/api/projects/${projectId}/access-rights`, {
        data: {
          email: user
        }
      })
      .then(() => {
        this.props.fetchProjectInfo();
        message.success('Access rights took away');
      });
  };

  wrapUserTag = user => {
    return (
      <div className="user-tag">
        <div className="start-xs">{user}</div>
        <div className="end-xs">
          <Button
            type="danger"
            size="small"
            icon="close"
            onClick={() => this.handleTakeAwayAccessRights(user)}
          />
        </div>
      </div>
    );
  };

  render() {
    const {
      accessGranted: { readAccess, writeAccess }
    } = this.props;

    const usersOptions = this.state.users.map(user => <Option key={user}>{user}</Option>);

    return (
      <div
        className="row access-granted-info"
        style={{ border: '1px solid #DDDDDD', margin: 'auto', padding: '8px 0'}}
      >
        <div className="access-granted-info__read col-xs-3">
          <div className="access-granted-info__read-users">
            <b>Read Access</b>
            {readAccess && readAccess.map(user => this.wrapUserTag(user))}
          </div>
        </div>
        <div className="access-granted-info__write col-xs-3">
          <div className="access-granted-info__write-users">
            <b>Write Access</b>
            {writeAccess && writeAccess.map(user => this.wrapUserTag(user))}
          </div>
        </div>
        <div className="access-granted-info__grant-access col-xs-6">
          <Select
            showSearch
            allowClear
            value={this.state.selectedUser}
            placeholder="Select user"
            style={{ width: 250, marginRight: '8px', marginBottom: '4px' }}
            defaultActiveFirstOption={false}
            showArrow={false}
            filterOption={false}
            onSearch={this.handleSearchUser}
            onChange={this.handleChangeUser}
            notFoundContent={null}
          >
            {usersOptions}
          </Select>
          <Select
            defaultValue="READ"
            style={{ width: 100, marginRight: '8px' }}
            onChange={this.handleAccessRightsTypeChange}
          >
            <Option value="READ">Read</Option>
            <Option value="WRITE">Write</Option>
          </Select>
          <Button
            className="access-granted-info__grant-access-btn"
            type="primary"
            onClick={() => this.handleAddAccessRights()}
          >
            Grant <Icon type="plus-circle" />
          </Button>
        </div>
      </div>
    );
  }
}

export default withRouter(AccessGrantedBlock);

AccessGrantedBlock.propTypes = {
  projectId: PropTypes.string.isRequired,
  accessGranted: PropTypes.shape({
    readAccess: PropTypes.arrayOf(PropTypes.string).isRequired,
    writeAccess: PropTypes.arrayOf(PropTypes.string).isRequired
  }).isRequired,
  fetchProjectInfo: PropTypes.func.isRequired
};
