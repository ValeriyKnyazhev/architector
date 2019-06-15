import React, { PureComponent } from 'react';
import axios from 'axios';
import { Button } from 'antd';
import _isEmpty from 'lodash/isEmpty';
import debounce from 'lodash/debounce';
import CodeEditor from 'components/CodeEditor';
import cn from 'classnames';
import './CodeResolveConflict.sass';

const ButtonGroup = Button.Group;

const rowClass = type =>
  cn({
    'code-conflict__row': true,
    'code-conflict__row--add': type === 'ADDITION',
    'code-conflict__row--delete': type === 'DELETION'
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
        conflictBlocks: state.conflictData.conflictBlocks.map((block, index) => {
          const contentItems = state.conflictData.oldContent.slice(
            block.startIndex === 0 ? 0 : block.startIndex - 1,
            block.endIndex === 0 ? block.endIndex : block.endIndex
          );
          return {
            ...block,
            conflictResolved: false,
            selectedData: contentItems,
            content: contentItems.join('\n'),
            appliedItems: [],
            id: index
          };
        })
      };
      this.setState({
        conflictData: conflictData,
        links: state.links
      });
    }
  }

  onUpdateContent = (contentState, blockId) => {
    const { conflictData } = this.state;
    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          return { ...block, content: String(contentState) };
        }
        return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    });
  };

  resolveConflict = async () => {
    const {
      conflictData: { oldContent, conflictBlocks, links, headCommitId }
    } = this.state;

    var startIndex = 1;
    var resultContent = '';
    var isInit = true;

    for (var index = 0; index < conflictBlocks.length; index++) {
      const currentBlock = conflictBlocks[index];
      const endIndex = currentBlock.startIndex - 1;

      if (startIndex <= endIndex) {
        if (isInit === false) {
          resultContent += '\n';
        }
        resultContent += oldContent.slice(startIndex - 1, endIndex).join('\n');
        isInit = false;
      }

      if (!_isEmpty(currentBlock.content)) {
        if (isInit === false) {
          resultContent += '\n';
        }
        resultContent += currentBlock.content;
        isInit = false;
      }

      startIndex = currentBlock.endIndex + 1;
    }

    resultContent += '\n' + oldContent.slice(startIndex - 1, oldContent.length).join('\n');

    await axios.post(links.resolveConflict, {
      headCommitId: headCommitId,
      content: resultContent
    });

    this.props.history.push({
      pathname: `/projects/${this.props.location.state.projectId}/files/${
        this.props.location.state.fileId
      }`
    });
  };

  removeHeadConflictValue = (blockId, removedItemIndex) => {
    const { conflictData } = this.state;
    var conflictResolved = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          conflictResolved = _isEmpty(resultBlocks) && _isEmpty(block.newBlocks);
          return { ...block, headBlocks: resultBlocks, conflictResolved };
        } else return block;
      })
    };

    this.setState(
      {
        conflictData: newConflictData
      },
      () => this.checkBlockConflicts(conflictResolved, blockId)
    );
  };

  removeNewDataConflictValue = (blockId, removedItemIndex) => {
    const { conflictData } = this.state;
    var conflictResolved = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          conflictResolved = _isEmpty(resultBlocks) && _isEmpty(block.headBlocks);
          return { ...block, newBlocks: resultBlocks, conflictResolved };
        } else return block;
      })
    };

    this.setState(
      {
        conflictData: newConflictData
      },
      () => this.checkBlockConflicts(conflictResolved, blockId)
    );
  };

  applyHeadConflictValue = (blockId, appliedItemIndex) => {
    const { conflictData } = this.state;
    var conflictResolved = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          conflictResolved = _isEmpty(resultBlocks) && _isEmpty(block.newBlocks);
          console.log(appliedItemIndex, block);
          block.appliedItems = block.appliedItems.concat(block.headBlocks[appliedItemIndex].items);
          return { ...block, headBlocks: resultBlocks, conflictResolved };
        } else return block;
      })
    };

    this.setState(
      {
        conflictData: newConflictData
      },
      () => this.checkBlockConflicts(conflictResolved, blockId)
    );
  };

  applyNewDataConflictValue = (blockId, appliedItemIndex) => {
    const { conflictData } = this.state;
    var conflictResolved = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          conflictResolved = _isEmpty(resultBlocks) && _isEmpty(block.headBlocks);
          console.log(appliedItemIndex, block);
          block.appliedItems = block.appliedItems.concat(block.newBlocks[appliedItemIndex].items);
          return { ...block, newBlocks: resultBlocks, conflictResolved };
        } else return block;
      })
    };

    this.setState(
      {
        conflictData: newConflictData
      },
      () => this.checkBlockConflicts(conflictResolved, blockId)
    );
  };

  checkBlockConflicts = (conflictResolved, blockId) => {
    if (conflictResolved) {
      this.applyAllChangesToData(blockId);
    }
  };

  applyAllChangesToData = blockId => {
    const { conflictData } = this.state;
    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const appliedItems = block.appliedItems.sort();
          var newData = block.selectedData;
          for (var itemIndex = appliedItems.length - 1; itemIndex >= 0; itemIndex--) {
            const item = appliedItems[itemIndex];
            if (item.type === 'ADDITION') {
              const index = item.position - block.startIndex + 1;
              newData.splice(index, 0, item.value);
            }
            if (item.type === 'DELETION') {
              const index = item.position - block.startIndex;
              newData.splice(index, 1);
            }
          }
          return { ...block, content: newData.join('\n'), appliedItems: [] };
        } else return block;
      })
    };
    this.setState({
      conflictData: newConflictData
    });
  };

  render() {
    if (_isEmpty(this.state.conflictData)) return null;
    const { conflictBlocks } = this.state.conflictData;

    console.log(conflictBlocks);
    return (
      <div className="container">
        <div>
          <h2>PLEASE RESOLVE CONFLICT</h2>
          <div className="row">
            <div className="col-xs-4">
              <h3>Head</h3>
            </div>
            <div className="col-xs-4">
              <h3>Old content</h3>
            </div>
            <div className="col-xs-4">
              <h3>New blocks</h3>
            </div>
          </div>
          <div style={{ marginBottom: 16 }}>
            {conflictBlocks.map(block => (
              <div className="row code-conflict__border" style={{ padding: '16px 0' }}>
                <div className="col-xs-4">
                  <div className="start-xs">
                    {block.headBlocks.map((head, index) => {
                      return (
                        <div className="d-flex s-between">
                          <div>
                            {head.items.map(item => (
                              <div>
                                <div className={rowClass(item.type)}>{item.value}</div>
                              </div>
                            ))}
                          </div>
                          <div style={{ minWidth: '50px', marginLeft: 8 }}>
                            <ButtonGroup>
                              <Button
                                type="primary"
                                size="small"
                                icon="close"
                                shape="circle"
                                onClick={() => {
                                  this.removeHeadConflictValue(block.id, index);
                                }}
                              />
                              <Button
                                type="primary"
                                size="small"
                                icon="right"
                                shape="circle"
                                onClick={() => {
                                  this.applyHeadConflictValue(block.id, index);
                                }}
                              />
                            </ButtonGroup>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
                <div className="col-xs-4">
                  <CodeEditor
                    content={block.content}
                    readOnly={block.conflictResolved ? false : true}
                    conflictResolved={block.conflictResolved}
                    onUpdateContent={contentState =>
                      block.conflictResolved && this.onUpdateContent(contentState, block.id)
                    }
                  />
                </div>
                <div className="col-xs-4">
                  <div className="end-xs">
                    {block.newBlocks.map((newBlock, index) => {
                      return (
                        <div className="d-flex s-between">
                          <div style={{ minWidth: '50px', marginRight: 8 }}>
                            <ButtonGroup>
                              <Button
                                type="primary"
                                size="small"
                                icon="left"
                                shape="circle"
                                onClick={() => {
                                  this.applyNewDataConflictValue(block.id, index);
                                }}
                              />
                              <Button
                                type="primary"
                                size="small"
                                icon="close"
                                shape="circle"
                                onClick={() => {
                                  this.removeNewDataConflictValue(block.id, index);
                                }}
                              />
                            </ButtonGroup>
                          </div>
                          <div>
                            {newBlock.items.map(item => (
                              <div>
                                <div className={rowClass(item.type)}>{item.value}</div>
                              </div>
                            ))}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              </div>
            ))}
          </div>
          <Button
            type="primary"
            disabled={conflictBlocks.some(block => !block.conflictResolved)}
            onClick={() => this.resolveConflict()}
          >
            RESOLVE
          </Button>
        </div>
      </div>
    );
  }
}
