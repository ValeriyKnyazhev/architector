import React, { PureComponent, Fragment } from 'react';
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
      const conflictData = {
        ...state.conflictData,
        conflictBlocks: state.conflictData.conflictBlocks.map((block, index) => ({
          ...block,
          selectedBlocks: state.conflictData.oldContent.slice(
            block.startIndex === 0 ? 0 : block.startIndex - 1,
            block.endIndex === 0 ? block.endIndex : block.endIndex
          ),
          id: index
        }))
      };
      this.setState({
        conflictData: conflictData
      });
    }
  }

  resolveConflict = async () => {
    // TO_DO добавить решение конфликта
    // await axios.post(this.state.links.resolveConflict, {
    //   headCommitId: this.state.headCommitId
    // });
  };

  removeHeadConflictValue = (id, removedBlock) => {
    const { conflictData } = this.state;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === id) {
          return { ...block, headBlocks: block.headBlocks };
        } else return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    });
  };

  removeNewDataConflictValue = removedBlock => {
    const { conflictData } = this.state;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === removedBlock.id) {
          return { ...block, newBlocks: [] };
        } else return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    });
  };

  applyNewDataConflictValue = applyBlock => {
    const { conflictData } = this.state;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === applyBlock.id) {
          return { ...block, newBlocks: [] };
        } else return block;
      }),
      selectedBlocks: applyBlock
    };
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
                          <div className="d-flex">
                            <div className={rowClass(item.type)}>{item.value}</div>
                            <div style={{ minWidth: '50px', marginLeft: 8 }}>
                              <ButtonGroup>
                                <Button
                                  type="primary"
                                  size="small"
                                  icon="close"
                                  shape="circle"
                                  onClick={() => {
                                    this.removeHeadConflictValue(block.id, item);
                                  }}
                                />
                                <Button
                                  type="primary"
                                  size="small"
                                  icon="right"
                                  shape="circle"
                                  onClick={() => {
                                    this.applyHeadConflictValue(block.id, item);
                                  }}
                                />
                              </ButtonGroup>
                            </div>
                          </div>
                        ))}
                      </div>
                    );
                  })}
                </div>
              </div>
              <div className="col-xs-4">
                <h3>Old content</h3>
                {block.selectedBlocks}
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
                            this.applyNewDataConflictValue(block);
                          }}
                        />
                        <Button
                          type="primary"
                          size="small"
                          icon="close"
                          shape="circle"
                          onClick={() => {
                            this.removeNewDataConflictValue(block);
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
          <Button type="primary" onClick={() => this.resolveConflict}>
            {' '}
            RESOLVE{' '}
          </Button>
        </div>
      </div>
    );
  }
}
