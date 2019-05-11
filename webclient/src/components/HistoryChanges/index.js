import React, { Component } from "react";
import PropTypes from "prop-types";
import { Button, Table } from "antd";

import "./HistoryChanges.css";

function renderCommitDate(date) {
  // TODO modify date if less certain value
  return new Date(date).toLocaleDateString();
}

const historyTableColumns = [
  {
    title: 'Author',
    dataIndex: 'author',
    key: 'author',
    width: 2
  },
  {
    title: 'Id',
    dataIndex: 'id',
    key: 'id',
    width: 1
  },
  {
    title: 'Parent Id',
    dataIndex: 'parentId',
    key: 'parentId',
    width: 1,
    render: parentId => <div>{parentId ? parentId : '-'}</div>
  },
  {
    title: 'Date',
    dataIndex: 'updated',
    key: 'updated',
    width: 2,
    render: date => <div>{renderCommitDate(date)}</div>
  },
  {
    title: 'Message',
    dataIndex: 'message',
    key: 'message',
    width: 6
  }
];

export default class HistoryChanges extends Component {
  state = {
    lastRecordsSize: 3,
    numberOfPages: 1
  };

  showMoreRecords = () => {
    this.setState({
      numberOfPages: this.state.numberOfPages + 1
    });
  };

  existMoreRecords = () => {
    const { numberOfPages } = this.state;
    const { isBriefModel, pageSize } = this.props;
    if (isBriefModel) return false;
    return numberOfPages * pageSize < this.props.commits.length;
  };

  calculateRecordsSize = () => {
    const { numberOfPages, lastRecordsSize } = this.state;
    const { pageSize, isBriefModel } = this.props;
    if (isBriefModel) {
      return lastRecordsSize;
    }
    const numberOfCommits = this.props.commits.length;
    return pageSize * numberOfPages < numberOfCommits ? pageSize * numberOfPages : numberOfCommits;
  };

  render() {
    const { isBriefModel, commits } = this.props;

    const recordsSize = this.calculateRecordsSize();

    const historyTableData = commits.slice(0, recordsSize).map((commit, index) => {
      return {
        key: index,
        author: commit.author,
        id: commit.id,
        parentId: commit.parentId,
        message: commit.message,
        date: commit.timestamp
        // showDiff: commit.id
      };
    });

    return (
      commits && (
        <div>
          <Table
            className="history-changes__commits-table"
            bordered
            columns={historyTableColumns}
            dataSource={historyTableData}
            pagination={false}
          />
          {this.existMoreRecords() && (
            <Button
              className="history-changes__show-more-records "
              onClick={() => this.showMoreRecords()}
              type="primary"
              style={{ margin: 10, alignContent: "center" }}
            >
              Show more
            </Button>
          )}
        </div>
      )
    );
  }
}

HistoryChanges.propTypes = {
  isBriefModel: PropTypes.bool,
  pageSize: PropTypes.number,
  commits: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      parentId: PropTypes.number,
      author: PropTypes.string.isRequired,
      message: PropTypes.string.isRequired,
      timestamp: PropTypes.string.isRequired
    })
  ).isRequired
};

HistoryChanges.defaultProps = {
  isBriefModel: true,
  pageSize: 10,
  commits: []
};
