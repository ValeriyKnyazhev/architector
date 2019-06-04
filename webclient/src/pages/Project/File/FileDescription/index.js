import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import FileStructureConflict from 'components/FileStructureConflict';
import { Table, Modal, message, Input, Button, Icon } from 'antd';

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

export default class FileDescription extends Component {
  state = {
    visibleEditDescription: false,
    visibleResolveConflict: false,
    conflict: {
      data: {
        descriptions: {},
        implementationLevel: {}
      },
      resolveLink: '',
      headCommitId: 0
    },
    newDescription: this.props.description.descriptions,
    newImplementationLevel: this.props.description.implementationLevel
  };

  onChangeValue = (event, valueName) => {
    this.setState({
      [valueName]: event.target.value
    });
  };

  onChangeValues = (event, index, valueName) => {
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

  handleEditDescription = modalVisible => {
    const { newDescription, newImplementationLevel } = this.state;
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    axios
      .put(`/api/projects/${projectId}/files/${fileId}/description`, {
        descriptions: newDescription,
        implementationLevel: newImplementationLevel,
        headCommitId: this.props.headCommitId
      })
      .then(({ data }) => {
        if (data.conflictData) {
          this.setState(
            {
              [modalVisible]: false,
              visibleResolveConflict: true,
              conflict: {
                data: data.conflictData,
                resolveLink: data.links.resolveConflict,
                headCommitId: data.headCommitId
              }
            },
            () => {
              this.props.fetchFileInfo();
              this.props.fetchFileHistoryChanges();
              message.success('Description was updated');
            }
          );
        } else {
          this.setState(
            {
              [modalVisible]: false
            },
            () => {
              this.props.fetchFileInfo();
              this.props.fetchFileHistoryChanges();
              message.success('Description was updated');
            }
          );
        }
      });
  };

  resolveConflict = newData => {
    const {
      conflict: { resolveLink, headCommitId }
    } = this.state;
    axios
      .post(resolveLink, {
        descriptions: newData.descriptions,
        implementationLevel: newData.implementationLevel,
        headCommitId: this.props.headCommitId
      })
      .then(() => {
        this.setState(
          {
            visibleResolveConflict: false
          },
          () => {
            this.props.fetchFileInfo();
            this.props.fetchFileHistoryChanges();
            message.success('Description conflict was resolved');
          }
        );
      });
  };

  render() {
    const { description, readOnly } = this.props;
    const {
      visibleEditDescription,
      visibleResolveConflict,
      conflict,
      newDescription,
      newImplementationLevel
    } = this.state;
    const descriptionData = [
      {
        key: '1',
        descriptions: description.descriptions,
        implementationLevel: description.implementationLevel
      }
    ];

    const conflictData = [
      { title: 'Descriptions', tag: 'descriptions', conflict: conflict.data.descriptions },
      {
        title: 'Implementation Level',
        tag: 'implementationLevel',
        conflict: conflict.data.implementationLevel
      }
    ];

    return (
      <div className="file__description">
        <div className="row file__description-header">
          <div className="col-xs-3" style={{ textAlign: 'left', marginBottom: '4px' }}>
            <b>Description</b>
            {!readOnly && (
              <Button
                type="primary"
                style={{ marginLeft: 8, alignContent: 'right' }}
                onClick={() => {
                  this.setState({
                    visibleEditDescription: true
                  });
                }}
              >
                <Icon type={'edit'} />
              </Button>
            )}
          </div>
          <div className="col-xs-9" />
        </div>
        <div className="file__description-info">
          <Table
            className="file__description-table"
            columns={descriptionColumns}
            dataSource={descriptionData}
            pagination={false}
          />
        </div>
        {visibleEditDescription && (
          <Modal
            title="Edit file description"
            visible={visibleEditDescription}
            onOk={() => this.handleEditDescription('visibleEditDescription')}
            onCancel={() => this.handleCancel('visibleEditDescription')}
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
                    placeholder="Add description"
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
            <div className="file__input-label">Implementation Level:</div>
            <Input
              placeholder="Add Implementation Level"
              className="file__multiply-input"
              value={newImplementationLevel}
              onChange={e => this.onChangeValue(e, 'newImplementationLevel')}
            />
          </Modal>
        )}
        {visibleResolveConflict && (
          <FileStructureConflict
            conflictData={conflictData}
            resolveConflict={this.resolveConflict}
            cancelChanges={() =>
              this.setState({
                visibleResolveConflict: false
              })
            }
          />
        )}
      </div>
    );
  }
}
