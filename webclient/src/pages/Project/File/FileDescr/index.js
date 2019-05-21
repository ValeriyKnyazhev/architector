import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Table, Tag, Modal, message, Input, Button, Icon } from 'antd';

const descriptionColumns = [
  {
    title: 'Descriptions',
    key: 'descriptions',
    dataIndex: 'descriptions',
    width: 9,
    render: descriptions => (
      <span>
        {descriptions.map(description => {
          return <div key={description}>{description}</div>;
        })}
      </span>
    )
  },
  {
    title: 'Implementation level',
    dataIndex: 'implementationLevel',
    key: 'implementationLevel',
    width: 3
  }
];

export default class FileMetadata extends Component {
  state = {
    visibleEditDescr: false,
    newDescription: this.props.file.description.descriptions,
    newImplementationLevel: this.props.file.description.implementationLevel
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

  onChangeValues = (event, index, valueName) => {
    const values = this.state[valueName];
    values[index] = event.target.value;
    console.log(values);
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

  handleEditDescr = modalVisible => {
    const { newDescription, newImplementationLevel } = this.state;
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    axios
      .put(`/api/projects/${projectId}/files/${fileId}/description`, {
        descriptions: newDescription,
        implementationLevel: newImplementationLevel
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false
          },
          () => {
            this.props.fetchFileInfo();
            this.props.fetchFileHistoryChanges();
            message.success('Descr was updated');
          }
        );
      });
  };

  render() {
    const { file } = this.props;
    const { visibleEditDescr, newDescription, newImplementationLevel } = this.state;
    const descriptionData = [
      {
        key: '1',
        descriptions: file.description.descriptions,
        implementationLevel: file.description.implementationLevel
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
                  visibleEditDescr: true
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
            columns={descriptionColumns}
            dataSource={descriptionData}
            pagination={false}
          />
        </div>
        {visibleEditDescr && (
          <Modal
            title="Edit file description"
            visible={visibleEditDescr}
            onOk={() => this.handleEditDescr('visibleEditDescr')}
            onCancel={() => this.handleCancel('visibleEditDescr')}
            okButtonProps={{
              disabled: _isEmpty(newDescription) || _isEmpty(newImplementationLevel)
            }}
          >
            <div className="file__multiply-edit">
              <div className="file__input-label">Descriptions:</div>
              <Button
                type="primary"
                style={{ marginLeft: 8, alignContent: 'right' }}
                onClick={() => this.addEmptyValues('newDescription')}
                icon="plus"
              />
            </div>
            <div className="file__multiply-inputs">
              {newDescription.map((value, index) => (
                <div className="file__multiply-input-container" key={index}>
                  <Input
                    placeholder="Add author"
                    className="file__multiply-input"
                    value={value}
                    onChange={e => this.onChangeValues(e, index, 'newDescription')}
                  />
                  <Button
                    type="danger"
                    style={{ marginLeft: 8, alignContent: 'right' }}
                    onClick={() => this.removeValue('newDescription', index)}
                    icon="minus"
                  />
                </div>
              ))}
            </div>
            <div className="file__input-label">OriginatingSystem:</div>
            <Input
              placeholder="Add OriginatingSystem"
              className="file__multiply-input"
              value={newImplementationLevel}
              onChange={e => this.onChangeValue(e, 'newImplementationLevel')}
            />
          </Modal>
        )}
      </div>
    );
  }
}
