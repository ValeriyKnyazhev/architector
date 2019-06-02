import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import _omit from 'lodash/omit';
import { Table, Modal, message, Input, Button, Icon } from 'antd';

const ButtonGroup = Button.Group;

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

export default class FileDescr extends Component {
  state = {
    visibleEditDescription: false,
    visibleResolveConflict: false,
    conflictSelectedValues: {
      descriptions: [],
      implementationLevel: ''
    },
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

  applyConflictValue = (tag, value) => {
    const newData = _omit(this.state.conflict.data, tag);
    this.setState({
      ...this.state,
      conflict: { ...this.state.conflict, ...{ data: newData } },
      conflictSelectedValues: { ...this.state.conflictSelectedValues, ...{ [tag]: value } }
    });
  };

  renderConflict = (title, tag) => {
    const conflict = this.state.conflict.data[tag];
    return (
      <div className={'conflict-' + tag}>
        <div className={'conflict-' + tag + '-header'}>{title}</div>
        {conflict ? (
          <div className={'row conflict-' + tag + '-values'}>
            <div className="col-xs-3">{conflict.headValue}</div>
            <div className="col-xs-3">{conflict.oldValue}</div>
            <div className="col-xs-3">{conflict.newValue}</div>
            <div className="col-xs-3">
              <ButtonGroup>
                {conflict.headValue && (
                  <Button
                    type="primary"
                    icon="left"
                    onClick={() => {
                      this.applyConflictValue(tag, conflict.headValue);
                    }}
                  />
                )}
                <Button
                  type="primary"
                  icon="close"
                  onClick={() => {
                    this.applyConflictValue(tag, conflict.oldValue);
                  }}
                />
                {conflict.newValue && (
                  <Button
                    type="primary"
                    icon="right"
                    onClick={() => {
                      this.applyConflictValue(tag, conflict.newValue);
                    }}
                  />
                )}
              </ButtonGroup>
            </div>
          </div>
        ) : (
          <div>{this.state.conflictSelectedValues[tag]}</div>
        )}
      </div>
    );
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

  resolveConflict = () => {
    const {
      conflict: { resolveLink, headCommitId },
      conflictSelectedValues
    } = this.state;
    axios
      .post(resolveLink, {
        descriptions: conflictSelectedValues.descriptions,
        implementationLevel: conflictSelectedValues.implementationLevel,
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
          <Modal
            title="Resolve file description conflict"
            visible={visibleResolveConflict}
            onOk={() => this.resolveConflict()}
            onCancel={() => this.handleCancel('visibleResolveConflict')}
            okButtonProps={{
              disabled: !_isEmpty(conflict.data)
            }}
          >
            <div className="row conflict-headers">
              <div className="col-xs-3">Head</div>
              <div className="col-xs-3">Old</div>
              <div className="col-xs-3">Your version</div>
              <div className="col-xs-3">Actions</div>
            </div>
            {this.renderConflict('Descriptions', 'descriptions', conflict.data.descriptions)}
            {this.renderConflict(
              'Implementation Level',
              'implementationLevel',
              conflict.data.implementationLevel
            )}
          </Modal>
        )}
      </div>
    );
  }
}
