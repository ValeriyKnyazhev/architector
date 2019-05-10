import React, { Component } from "react";
import PropTypes from "prop-types";
import { Table } from "antd";

import "./HistoryChanges.css";

function renderCommitDate(date) {
  // TODO modify date if less certain value
  return new Date(date).toLocaleDateString();
}

const historyTableColumns = [
  {
    title: "Author",
    dataIndex: "author",
    key: "author",
    width: 2
  },
  {
    title: "Id",
    dataIndex: "id",
    key: "id",
    width: 1
  },
  {
    title: "Parent Id",
    dataIndex: "parentId",
    key: "parentId",
    width: 1,
    render: parentId => <div>{parentId ? parentId : "-"}</div>
  },
  {
    title: "Date",
    dataIndex: "updated",
    key: "updated",
    width: 2,
    render: date => <div>{renderCommitDate(date)}</div>
  },
  {
    title: "Message",
    dataIndex: "message",
    key: "message",
    width: 6
  }
];

export default class HistoryChanges extends Component {
  state = {
    pageSize: 10,
    lastRecordsSize: 3
  };

  render() {
    const { pageSize, lastRecordsSize } = this.state;
    const { isBriefModel, commits } = this.props;
    console.log("commits", commits);
    const shownCommits = isBriefModel ? commits.slice(0, lastRecordsSize) : commits;
    console.log("shown commits", shownCommits);

    const historyTableData = shownCommits.map((commit, index) => {
      return {
        key: index,
        author: commit.author,
        id: commit.id,
        parentId: commit.parentId,
        message: commit.message,
        date: commit.timestamp
      };
    });

    return (
      commits && (
        <Table
          className="history-changes__commits-table"
          bordered
          columns={historyTableColumns}
          dataSource={historyTableData}
          pagination={false}
        />
      )
    );
  }
}

HistoryChanges.propTypes = {
  isBriefModel: PropTypes.bool,
  commits: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      parentId: PropTypes.string,
      author: PropTypes.string.isRequired,
      message: PropTypes.string.isRequired,
      timestamp: PropTypes.string.isRequired
    })
  ).isRequired
};

HistoryChanges.defaultProps = {
  isBriefModel: true,
  commits: []
};
