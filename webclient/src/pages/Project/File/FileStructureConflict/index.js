import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router';
import _isEmpty from 'lodash/isEmpty';
import _omit from 'lodash/omit';
import { Button, Modal } from 'antd';

const ButtonGroup = Button.Group;

class FileStructureConflict extends Component {
  state = {
    selectedFields: {},
    conflicts: {}
  };

  componentDidMount() {
    var conflicts = {};
    var selectedFields = {};
    this.props.conflictData.map(elem => {
      conflicts[elem.tag] = elem.conflict;
      selectedFields[elem.tag] = elem.conflict.oldValue;
    });
    this.setState({ conflicts, selectedFields });
  }

  applyHeadConflictValue = (tag, value) => {
    const { selectedFields, conflicts } = this.state;
    const newData = conflicts[tag].newValue
      ? {
          ...conflicts,
          ...{ [tag]: _omit(conflicts[tag], 'headValue') }
        }
      : _omit(conflicts, tag);
    console.log(newData)
    this.setState({
      ...this.state,
      conflicts: newData,
      selectedFields: { ...selectedFields, ...{ [tag]: value } }
    });
  };

  removeHeadConflictValue = tag => {
    const { conflicts } = this.state;
    const newData = conflicts[tag].newValue
      ? {
          ...conflicts,
          ...{ [tag]: _omit(conflicts[tag], 'headValue') }
        }
      : _omit(conflicts, tag);
    this.setState({
      ...this.state,
      conflicts: newData
    });
  };

  applyNewDataConflictValue = (tag, value) => {
    const { selectedFields, conflicts } = this.state;
    const newData = conflicts[tag].headValue
      ? {
          ...conflicts,
          ...{ [tag]: _omit(conflicts[tag], 'newValue') }
        }
      : _omit(conflicts, tag);
    this.setState({
      ...this.state,
      conflicts: newData,
      selectedFields: { ...selectedFields, ...{ [tag]: value } }
    });
  };

  removeNewDataConflictValue = tag => {
    const { conflicts } = this.state;
    const newData = conflicts[tag].headValue
      ? {
          ...conflicts,
          ...{ [tag]: _omit(conflicts[tag], 'newValue') }
        }
      : _omit(conflicts, tag);
    this.setState({
      ...this.state,
      conflicts: newData
    });
  };

  renderConflict = (title, tag, conflict) => {
    const selectedField = this.state.selectedFields[tag];
    return conflict ? (
      <div className={'conflict-' + tag}>
        <div className={'conflict-' + tag + '-header'}>{title}</div>
        <div className={'row conflict-' + tag + '-value'}>
          <div className="col-xs-3">{conflict.headValue}</div>
          <div className="col-xs-1">
            {conflict.headValue && (
              <ButtonGroup>
                <Button
                  type="primary"
                  size="small"
                  icon="close"
                  onClick={() => {
                    this.removeHeadConflictValue(tag);
                  }}
                />
                <Button
                  type="primary"
                  size="small"
                  icon="right"
                  onClick={() => {
                    this.applyHeadConflictValue(tag, conflict.headValue);
                  }}
                />
              </ButtonGroup>
            )}
          </div>
          <div className="col-xs-4">{selectedField}</div>
          <div className="col-xs-1">
            {conflict.newValue && (
              <ButtonGroup>
                <Button
                  type="primary"
                  size="small"
                  icon="left"
                  onClick={() => {
                    this.applyNewDataConflictValue(tag, conflict.newValue);
                  }}
                />
                <Button
                  type="primary"
                  size="small"
                  icon="close"
                  onClick={() => {
                    this.removeNewDataConflictValue(tag);
                  }}
                />
              </ButtonGroup>
            )}
          </div>
          <div className="col-xs-3">{conflict.newValue}</div>
        </div>
      </div>
    ) : (
      <div className={'row conflict-' + tag}>
        <div className={'col-xs-4 conflict-' + tag + '-header'}>{title}: </div>
        <div className={'col-xs-8 conflict-' + tag + '-value'}>{selectedField}</div>
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

  render() {
    const { resolveConflict, cancelChanges } = this.props;
    const { conflicts, selectedFields } = this.state;

    console.log('Conflicts', conflicts);
    console.log('Selected', selectedFields);

    return (
      <Modal
        title="Resolve file description conflict"
        visible={true}
        width={800}
        onOk={() => resolveConflict(selectedFields)}
        onCancel={() => cancelChanges()}
        okButtonProps={{
          disabled: !_isEmpty(conflicts)
        }}
      >
        <div className="row conflict-headers">
          <div className="col-xs-4">Head</div>
          <div className="col-xs-4">Old</div>
          <div className="col-xs-4">Your version</div>
        </div>
        {this.props.conflictData.map(elem =>
          this.renderConflict(elem.title, elem.tag, conflicts[elem.tag])
        )}
      </Modal>
    );
  }
}

export default withRouter(FileStructureConflict);

FileStructureConflict.propTypes = {
  conflictData: PropTypes.arrayOf(
    PropTypes.shape({
      title: PropTypes.string.isRequired,
      tag: PropTypes.string.isRequired,
      conflict: PropTypes.shape({
        oldValue: PropTypes.any.isRequired,
        newValue: PropTypes.any,
        headValue: PropTypes.any
      }).isRequired
    })
  ).isRequired,
  resolveConflict: PropTypes.func.isRequired,
  cancelChanges: PropTypes.func.isRequired
};
