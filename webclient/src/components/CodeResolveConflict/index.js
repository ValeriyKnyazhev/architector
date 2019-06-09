import React, { PureComponent } from 'react';
import { Button, Modal } from 'antd';
import _isEmpty from 'lodash/isEmpty';
import cn from 'classnames';
import axios from 'axios';
import './CodeResolveConflict.sass';

const ButtonGroup = Button.Group;

const rowClass = type =>
  cn({
    commit__row: true,
    'commit__row--add': type === 'ADDITION',
    'commit__row--delete': type === 'DELETION'
  });

export default class CodeResolveConflict extends PureComponent {
  state = { conflictData: {} };

  componentDidMount() {
    const {
      location: { state }
    } = this.props;

    if (state.conflictData) {
      this.setState({
        conflictData: state.conflictData
      });
    }
  }

  resolveCommit = async () => {
    await axios.post(this.state.links.resolveConflict, {
      headCommitId: this.state.headCommitId
    });
  };

  render() {
    if (_isEmpty(this.state.conflictData)) return null;
    const { conflictBlocks, oldContent } = this.state.conflictData;
    return (
      <div className="container">
        <div>
          <h2>PLEASE RESOLVE CONFLICT</h2>

          {conflictBlocks.map(block => (
            <div className="row" style={{ margin: '16px 0' }}>
              <div className="col-xs-4">
                <h3>Head</h3>
                <div className="d-flex">
                  {block.headBlocks.map(head => {
                    return (
                      <div>
                        {head.items.map(item => (
                          <div className={rowClass(item.type)}>{item.value}</div>
                        ))}
                      </div>
                    );
                  })}
                  <div style={{ minWidth: '50px', marginLeft: 8 }}>
                    {block.headBlocks.length > 0 && (
                      <ButtonGroup>
                        <Button
                          type="primary"
                          size="small"
                          icon="close"
                          shape="circle"
                          onClick={() => {
                            //this.removeHeadConflictValue();
                          }}
                        />
                        <Button
                          type="primary"
                          size="small"
                          icon="right"
                          shape="circle"
                          onClick={() => {
                            //this.applyHeadConflictValue();
                          }}
                        />
                      </ButtonGroup>
                    )}
                  </div>
                </div>
              </div>
              <div className="col-xs-4">
                <h3>Old content</h3>
                {oldContent.slice(
                  block.startIndex - 1 > 0 ? block.startIndex - 1 : 0,
                  block.endIndex === 0 ? block.endIndex + 1 : block.endIndex
                )}
              </div>
              <div className="col-xs-4">
                <h3>New blocks</h3>
                <div className="d-flex">
                  <div style={{ minWidth: '50px', marginRight: 8 }}>
                    {block.newBlocks.length > 0 && (
                      <ButtonGroup>
                        <Button
                          type="primary"
                          size="small"
                          icon="left"
                          shape="circle"
                          onClick={() => {
                            //this.applyNewDataConflictValue();
                          }}
                        />
                        <Button
                          type="primary"
                          size="small"
                          icon="close"
                          shape="circle"
                          onClick={() => {
                            //this.removeNewDataConflictValue();
                          }}
                        />
                      </ButtonGroup>
                    )}
                  </div>
                  {block.newBlocks.map(block => {
                    return (
                      <div>
                        {block.items.map(item => (
                          <div className={rowClass(item.type)}>{item.value}</div>
                        ))}
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }
}
