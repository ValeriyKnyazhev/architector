import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Table, Tag, Modal, message, Input, Button, Icon } from 'antd';

const metadataColumns = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    width: 3
  },
  {
    title: 'Authors',
    key: 'authors',
    dataIndex: 'authors',
    width: 3,
    render: authors => (
      <span>
        {authors.map(author => {
          return (
            <Tag color="geekblue" key={author}>
              {author}
            </Tag>
          );
        })}
      </span>
    )
  },
  {
    title: 'Organizations',
    key: 'organizations',
    dataIndex: 'organizations',
    width: 2,
    render: organizations => (
      <span>
        {organizations.map(organization => {
          return (
            <Tag color="geekblue" key={organization}>
              {organization.toUpperCase()}
            </Tag>
          );
        })}
      </span>
    )
  },
  {
    title: 'Originating system',
    dataIndex: 'originatingSystem',
    key: 'originatingSystem',
    width: 2
  },
  {
    title: 'Preprocessor version',
    dataIndex: 'preprocessorVersion',
    key: 'preprocessorVersion',
    width: 2
  }
];

export default class FileMetadata extends Component {
  state = {
    visibleEditMetada: false,
    newAuthors: this.props.file.metadata.authors,
    newOrganizations: this.props.file.metadata.organizations,
    newName: this.props.file.metadata.name,
    newOriginatingSystem: this.props.file.metadata.originatingSystem,
    newPreprocessorVersion: this.props.file.metadata.preprocessorVersion
  };

  handleCancel = state => {
    this.setState({
      [state]: false
    });
  };

  onChangeValue = (event, valueName) => {
    this.setState({
      [valueName]: event.target.value
    });
  };

  onChangeMetadataValues = (event, index, valueName) => {
    const values = this.state[valueName];
    values[index] = event.target.value;
    this.setState({
      [valueName]: values
    });
  };

  addEmptyValues = valueName => {
    const values = this.state[valueName];
    values.push('');
    this.setState({
      [valueName]: values
    });
  };

  removeValue = (valueName, index) => {
    const values = this.state[valueName];
    const newValues = values.filter((value, i) => i !== index);
    this.setState({
      [valueName]: newValues
    });
  };

  handleCancel = state => {
    this.setState({
      [state]: false
    });
  };

  handleEditMetadata = modalVisible => {
    const {
      newName,
      newAuthors,
      newOrganizations,
      newOriginatingSystem,
      newPreprocessorVersion
    } = this.state;
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    axios
      .put(`/api/projects/${projectId}/files/${fileId}/metadata`, {
        ...this.props.file.metadata,
        name: newName,
        authors: newAuthors,
        organizations: newOrganizations,
        preprocessorVersion: newPreprocessorVersion,
        originatingSystem: newOriginatingSystem
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false
          },
          () => {
            this.props.fetchFileInfo();
            this.props.fetchFileHistoryChanges();
            message.success('Metadata was updated');
          }
        );
      });
  };

  render() {
    const { file } = this.props;
    const { visibleEditMetada, newAuthors, newOrganizations, newName } = this.state;
    const metadataData = [
      {
        key: '1',
        name: file.metadata.name,
        authors: file.metadata.authors,
        organizations: file.metadata.organizations,
        originatingSystem: file.metadata.originatingSystem,
        preprocessorVersion: file.metadata.preprocessorVersion
      }
    ];
    return (
      <div className="file__metadata">
        <div className="row file__metadata-header">
          <div className="col-xs-3" style={{ textAlign: 'left', marginBottom: '4px' }}>
            <b>Metadata</b>
            <Button
              type="primary"
              style={{ marginLeft: 8, alignContent: 'right' }}
              onClick={() => {
                this.setState({
                  visibleEditMetada: true
                });
              }}
            >
              <Icon type={'edit'} />
            </Button>
          </div>
          <div className="col-xs-9" />
        </div>
        <div className="file__metadata-info">
          <Table
            className="file__metadata-table"
            columns={metadataColumns}
            dataSource={metadataData}
            pagination={false}
          />
        </div>
        {visibleEditMetada && (
          <Modal
            title="Edit file metadata"
            visible={visibleEditMetada}
            onOk={() => this.handleEditMetadata('visibleEditMetada')}
            onCancel={() => this.handleCancel('visibleEditMetada')}
            okButtonProps={{
              disabled: _isEmpty(newAuthors) || _isEmpty(newOrganizations) || _isEmpty(newName)
            }}
          >
            <div className="file__input-label">Name:</div>
            <Input
              placeholder="Add author"
              className="file__multiply-input"
              value={newName}
              onChange={e => this.onChangeValue(e, 'newName')}
            />
            <div className="file__multiply-edit">
              <div className="file__input-label">Authors:</div>
              <Button
                type="primary"
                style={{ marginLeft: 8, alignContent: 'right' }}
                onClick={() => this.addEmptyValues('newAuthors')}
                icon="plus"
              />
            </div>
            <div className="file__multiply-inputs">
              {newAuthors.map((value, index) => (
                <div className="file__multiply-input-container">
                  <Input
                    placeholder="Add author"
                    className="file__multiply-input"
                    value={value}
                    onChange={e => this.onChangeMetadataValues(e, index, 'newAuthors')}
                  />
                  <Button
                    type="danger"
                    style={{ marginLeft: 8, alignContent: 'right' }}
                    onClick={() => this.removeValue('newAuthors', index)}
                    icon="minus"
                  />
                </div>
              ))}
            </div>
            <div className="file__multiply-edit">
              <div className="file__input-label">Organizations:</div>
              <Button
                type="primary"
                style={{ marginLeft: 8, alignContent: 'right' }}
                onClick={() => this.addEmptyValues('newOrganizations')}
                icon="plus"
              />
            </div>
            <div className="file__multiply-inputs">
              {newOrganizations.map((value, index) => (
                <div className="file__multiply-input-container">
                  <Input
                    placeholder="Add organization"
                    className="file__multiply-input"
                    value={value}
                    onChange={e => this.onChangeMetadataValues(e, index, 'newOrganizations')}
                  />
                  <Button
                    type="danger"
                    style={{ marginLeft: 8, alignContent: 'right' }}
                    onClick={() => this.removeValue('newOrganizations', index)}
                    icon="minus"
                  />
                </div>
              ))}
            </div>
          </Modal>
        )}
      </div>
    );
  }
}
