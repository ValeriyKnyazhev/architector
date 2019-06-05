import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';
import { Button, Icon, Table } from 'antd';

import './HistoryChanges.css';

const ONE_SECOND = 1000;
const ONE_MINUTE = ONE_SECOND * 60;
const ONE_HOUR = ONE_MINUTE * 60;
const ONE_DAY = ONE_HOUR * 24;

function renderCommitDate(date) {
  // TODO modify date if less certain value
  const diff = Date.now() - new Date(date).getTime();
  const days = Math.floor(diff / ONE_DAY);
  if (days >= 28) {
    return date.toDateString();
  } else if (days >= 7) {
    return days / 7 + ' weeks ago';
  } else if (days >= 1) {
    return days + ' days ago';
  } else {
    const hours = Math.floor(diff / ONE_HOUR);
    if (hours >= 1) {
      return hours + ' hours ago';
    }
    const minutes = Math.floor(diff / ONE_MINUTE);
    if (minutes >= 1) {
      return minutes + ' minutes ago';
    } else {
      return Math.floor(diff / ONE_SECOND) + ' seconds ago';
    }
  }
}

const historyTableColumns = props => {
  const {
    match: {
      params: { projectId, fileId }
    }
  } = props;
  return [
    {
      title: 'Author',
      dataIndex: 'author',
      key: 'author',
      width: 2,
      align: 'left'
    },
    {
      title: 'Id',
      dataIndex: 'id',
      key: 'id',
      width: 1,
      align: 'center'
    },
    {
      title: 'Parent Id',
      dataIndex: 'parentId',
      key: 'parentId',
      width: 1,
      align: 'center',
      render: parentId => <div>{parentId ? parentId : '-'}</div>
    },
    {
      title: 'Message',
      dataIndex: 'message',
      key: 'message',
      width: 6,
      align: 'left'
    },
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
      width: 2,
      align: 'center',
      render: date => <div>{renderCommitDate(date)}</div>
    },
    {
      key: 'action',
      width: 2,
      render: record => (
        <div>
          <Link
            to={{
              pathname: `/projects/${projectId}/changes/${record.id}/diff`
            }}
            style={{ margin: '0 8px' }}
          >
            <Icon type="diff" style={{ fontSize: '24px' }} />
          </Link>
          <Link
            to={{
              pathname: fileId
                ? `/projects/${projectId}/files/${fileId}/changes/${record.id}/content`
                : `/projects/${projectId}/changes/${record.id}/content`
            }}
            style={{ margin: '0 8px' }}
          >
            <Icon type="file" style={{ fontSize: '24px' }} />
          </Link>
          {fileId && (
            <Link
              to={{
                pathname: `api/projects/${projectId}/files/${fileId}/changes/${record.id}/download`
              }}
              style={{ margin: '0 8px' }}
            >
              <Icon type="download" style={{ fontSize: '24px' }} />
            </Link>
          )}
        </div>
      )
    }
  ];
};

class HistoryChanges extends Component {
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
    const { commits } = this.props;

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
            columns={historyTableColumns(this.props)}
            dataSource={historyTableData}
            pagination={false}
          />
          {this.existMoreRecords() && (
            <Button
              className="history-changes__show-more-records "
              onClick={() => this.showMoreRecords()}
              type="primary"
              style={{ margin: 10, alignContent: 'center' }}
            >
              Show more
            </Button>
          )}
        </div>
      )
    );
  }
}

export default withRouter(HistoryChanges);

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
