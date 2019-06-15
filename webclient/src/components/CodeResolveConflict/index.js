import React, { PureComponent } from "react";
import { Button } from "antd";
import _isEmpty from "lodash/isEmpty";
import cn from "classnames";
import "./CodeResolveConflict.sass";

const ButtonGroup = Button.Group;

const rowClass = type =>
  cn({
    commit__row: true,
    "commit__row--add": type === "ADDITION",
    "commit__row--delete": type === "DELETION"
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
            content: contentItems.join("\n"),
            appliedItems: [],
            id: index
          };
        })
      };
      this.setState({
        conflictData: conflictData
      });
    }
  }

  resolveConflict = async () => {
    const { conflictData } = this.state;

    // await axios.post(this.state.links.resolveConflict, {
    //   headCommitId: this.state.headCommitId
    // });
  };

  removeHeadConflictValue = (blockId, removedItemIndex) => {
    const { conflictData } = this.state;
var resolvedConflicts = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          resolvedConflicts = _isEmpty(resultBlocks) && _isEmpty(block.newBlocks)
          return { ...block, headBlocks: resultBlocks, resolvedConflicts };
        } else return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    }, () => this.checkBlockConflicts(resolvedConflicts, blockId));
  };

  removeNewDataConflictValue = (blockId, removedItemIndex) => {
    const { conflictData } = this.state;
var resolvedConflicts = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          resolvedConflicts = _isEmpty(resultBlocks) && _isEmpty(block.headBlocks)
          return { ...block, newBlocks: resultBlocks, resolvedConflicts };
        } else return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    }, () => this.checkBlockConflicts(resolvedConflicts, blockId));
  };

  applyHeadConflictValue = (blockId, appliedItemIndex) => {
    const { conflictData } = this.state;
var resolvedConflicts = false;

    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          resolvedConflicts = _isEmpty(resultBlocks) && _isEmpty(block.newBlocks)
          block.appliedItems = block.appliedItems.concat(block.headBlocks[appliedItemIndex].items);
          return { ...block, headBlocks: resultBlocks, resolvedConflicts };
        } else return block;
      })
    };

    this.setState({
      conflictData: newConflictData
    }, () => this.checkBlockConflicts(resolvedConflicts, blockId));
  };

  applyNewDataConflictValue = (blockId, appliedItemIndex) => {
    const { conflictData } = this.state;
    var resolvedConflicts = false;
    const newConflictData = {
      ...conflictData,
      conflictBlocks: conflictData.conflictBlocks.map(block => {
        if (block.id === blockId) {
          const resultBlocks = [];
          resolvedConflicts = _isEmpty(resultBlocks) && _isEmpty(block.headBlocks)
          block.appliedItems = block.appliedItems.concat(block.newBlocks[appliedItemIndex].items);
          return { ...block, newBlocks: resultBlocks, resolvedConflicts };
        } else return block;
      })
    };

    this.setState(
      {
        conflictData: newConflictData
      },
      () => this.checkBlockConflicts(resolvedConflicts, blockId)
    );
  };

  checkBlockConflicts = (conflictResolved, blockId) => {
    if (conflictResolved) {
      this.applyAllChangesToData(blockId)
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
            if (item.type === "ADDITION") {
              const index = item.position;
              newData.splice(index, 0, item.value);
            }
            if (item.type === "DELETION") {
              const index = item.position - 1;
              newData.splice(index, 1);
            }
          }
          return { ...block, content: newData.join("\n"), appliedItems: [] };
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
    console.log("CONFLICTS", conflictBlocks);
    return (
      <div className="container">
        <div>
          <h2>PLEASE RESOLVE CONFLICT</h2>
          {conflictBlocks.map(block => (
            <div className="row" style={{ margin: "16px 0" }}>
              <div className="col-xs-4">
                <h3>Head</h3>
                <div className="d-flex">
                  {block.headBlocks.map(head => {
                    return (
                      <div>
                        {head.items.map((item, index) => (
                          <div className="d-flex">
                            <div className={rowClass(item.type)}>{item.value}</div>
                            <div style={{ minWidth: "50px", marginLeft: 8 }}>
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
                        ))}
                      </div>
                    );
                  })}
                </div>
              </div>
              <div className="col-xs-4">
                <h3>Old content</h3>
                {block.content}
              </div>
              <div className="col-xs-4">
                <h3>New blocks</h3>
                <div className="d-flex end-xs">
                  {block.newBlocks.map(newBlock => {
                    return (
                      <div>
                        {newBlock.items.map((item, index) => (
                          <div className="d-flex">
                            <div style={{ minWidth: "50px", marginRight: 8 }}>
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
                            <div className={rowClass(item.type)}>{item.value}</div>
                          </div>
                        ))}
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          ))}
          <Button type="primary" onClick={() => this.resolveConflict()}>
            {" "}
            RESOLVE{" "}
          </Button>
        </div>
      </div>
    );
  }
}
