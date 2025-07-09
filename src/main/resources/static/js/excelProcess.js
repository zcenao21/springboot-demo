// Excel文件处理功能
document.addEventListener('DOMContentLoaded', function() {
    // 文件选择监听
    const fileInputA = document.getElementById('csvA');
    const fileInputB = document.getElementById('csvB');

    if (fileInputA) {
        fileInputA.addEventListener('change', function(e) {
            updateFileInfo('fileInfoA', e.target.files[0]);
        });
    }

    if (fileInputB) {
        fileInputB.addEventListener('change', function(e) {
            updateFileInfo('fileInfoB', e.target.files[0]);
        });
    }
});

function updateFileInfo(infoId, file) {
    const infoElement = document.getElementById(infoId);
    if (file) {
        infoElement.textContent = `已选择: ${file.name} (${formatFileSize(file.size)})`;
        infoElement.classList.add('has-file');
    } else {
        infoElement.textContent = '未选择文件';
        infoElement.classList.remove('has-file');
    }
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function startMatching() {
    const fileA = document.getElementById('csvA').files[0];
    const fileB = document.getElementById('csvB').files[0];

    if (!fileA || !fileB) {
        alert('请先上传两个CSV文件！');
        return;
    }
// 显示结果区域和加载状态
    const resultsDiv = document.getElementById('matchResults');
    const resultsContent = resultsDiv.querySelector('.results-content');
    resultsDiv.style.display = 'block';

    resultsContent.innerHTML = `
        <div class="loading">
            <p>🔄 正在处理文件...</p>
            <p>文件A: ${fileA.name}</p>
            <p>文件B: ${fileB.name}</p>
            <div class="progress-bar">
                <div class="progress-fill"></div>
            </div>
        </div>
    `;

    // 创建FormData对象
    const formData = new FormData();
    formData.append('fileA', fileA);
    formData.append('fileB', fileB);

    // 调用后端API
    fetch('/api/excel/match', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        displayMatchResults(data);
    })
    .catch(error => {
        console.error('匹配过程中发生错误:', error);
        resultsContent.innerHTML = `
            <div class="error">
                <p>❌ 匹配失败</p>
                <p>错误信息: ${error.message}</p>
                <p>请检查文件格式是否正确，或稍后重试。</p>
            </div>
        `;
    });
}


// 显示匹配结果的函数
function displayMatchResults(data) {
    const resultsContent = document.querySelector('.results-content');

    if (data.success) {
        resultsContent.innerHTML = `
            <div class="success">
                <h5>✅ 匹配完成</h5>
                <div class="match-summary">
                    <p><strong>总记录数:</strong> ${data.totalRecords || 0}</p>
                    <p><strong>匹配记录:</strong> ${data.matchedRecords || 0}</p>
                    <p><strong>不匹配记录:</strong> ${data.unmatchedRecords || 0}</p>
                    <p><strong>匹配率:</strong> ${data.matchRate || '0%'}</p>
                </div>

                ${data.matchDetails ? `
                <div class="match-details">
                    <h6>匹配详情:</h6>
                    <div class="details-table">
                        ${generateMatchTable(data.matchDetails)}
                    </div>
                </div>
                ` : ''}

                ${data.downloadUrl ? `
                <div class="download-section">
                    <a href="${data.downloadUrl}" class="btn btn-success" download>
                        📥 下载匹配结果
                    </a>
                </div>
                ` : ''}
            </div>
        `;
    } else {
        resultsContent.innerHTML = `
            <div class="error">
                <p>❌ 匹配失败</p>
                <p>${data.message || '未知错误'}</p>
            </div>
        `;
    }
}

// 生成匹配结果表格
function generateMatchTable(matchDetails) {
    if (!matchDetails || matchDetails.length === 0) {
        return '<p>暂无详细匹配信息</p>';
    }

    let tableHtml = `
        <table class="match-table">
            <thead>
                <tr>
                    <th>序号</th>
                    <th>文件A数据</th>
                    <th>文件B数据</th>
                    <th>匹配状态</th>
                </tr>
            </thead>
            <tbody>
    `;

    matchDetails.forEach((item, index) => {
        const statusClass = item.matched ? 'matched' : 'unmatched';
        const statusText = item.matched ? '✅ 匹配' : '❌ 不匹配';

        tableHtml += `
            <tr class="${statusClass}">
                <td>${index + 1}</td>
                <td>${item.dataA || '-'}</td>
                <td>${item.dataB || '-'}</td>
                <td>${statusText}</td>
            </tr>
        `;
    });

    tableHtml += `
            </tbody>
        </table>
    `;

    return tableHtml;
}

// 添加进度条和样式支持
function addProgressStyles() {
    if (!document.getElementById('excel-progress-styles')) {
        const style = document.createElement('style');
        style.id = 'excel-progress-styles';
        style.textContent = `
            .loading {
                text-align: center;
                padding: 20px;
            }

            .progress-bar {
                width: 100%;
                height: 6px;
                background: #e9ecef;
                border-radius: 3px;
                margin: 15px 0;
                overflow: hidden;
            }

            .progress-fill {
                height: 100%;
                background: linear-gradient(90deg, #007bff, #0056b3);
                width: 0%;
                animation: progress 2s ease-in-out infinite;
            }

            @keyframes progress {
                0% { width: 0%; }
                50% { width: 70%; }
                100% { width: 100%; }
            }

            .success {
                color: #155724;
                background: #d4edda;
                padding: 15px;
                border-radius: 5px;
                border: 1px solid #c3e6cb;
            }

            .error {
                color: #721c24;
                background: #f8d7da;
                padding: 15px;
                border-radius: 5px;
                border: 1px solid #f5c6cb;
            }

            .match-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 15px;
            }

            .match-table th,
            .match-table td {
                border: 1px solid #dee2e6;
                padding: 8px 12px;
                text-align: left;
            }

            .match-table th {
                background: #f8f9fa;
                font-weight: bold;
            }

            .match-table .matched {
                background: #d4edda;
            }

            .match-table .unmatched {
                background: #f8d7da;
            }

            .download-section {
                margin-top: 20px;
                text-align: center;
            }
        `;
        document.head.appendChild(style);
    }
}

function clearFiles() {
    document.getElementById('excelA').value = '';
    document.getElementById('excelB').value = '';
    updateFileInfo('fileInfoA', null);
    updateFileInfo('fileInfoB', null);

    const resultsDiv = document.getElementById('matchResults');
    resultsDiv.style.display = 'none';
}

// 页面加载时添加样式
document.addEventListener('DOMContentLoaded', function() {
    addProgressStyles();
});