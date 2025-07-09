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
    document.getElementById('csvA').value = '';
    document.getElementById('csvB').value = '';
    document.getElementById('fileInfoA').textContent = '未选择文件';
    document.getElementById('fileInfoB').textContent = '未选择文件';
    document.getElementById('previewA').style.display = 'none';
    document.getElementById('previewB').style.display = 'none';

    // 清空列名和匹配规则
    columnsA = [];
    columnsB = [];
    document.getElementById('matchRulesSection').style.display = 'none';
    document.getElementById('rulesList').innerHTML = '';
    additionalRuleCount = 0;
}

// 页面加载时添加样式
document.addEventListener('DOMContentLoaded', function() {
    addProgressStyles();
});

// 添加文件预览功能
document.addEventListener('DOMContentLoaded', function() {
    const csvAInput = document.getElementById('csvA');
    const csvBInput = document.getElementById('csvB');

    if (csvAInput) {
        csvAInput.addEventListener('change', function(e) {
            handleFileSelect(e, 'A');
        });
    }

    if (csvBInput) {
        csvBInput.addEventListener('change', function(e) {
            handleFileSelect(e, 'B');
        });
    }
});

// 全局变量存储列名
let columnsA = [];
let columnsB = [];
let additionalRuleCount = 0;

// 修改文件选择处理函数
function handleFileSelect(event, fileType) {
    const file = event.target.files[0];
    const fileInfoId = `fileInfo${fileType}`;
    const previewId = `preview${fileType}`;
    const previewContentId = `previewContent${fileType}`;

    if (file) {
        document.getElementById(fileInfoId).textContent =
            `${file.name} (${(file.size / 1024).toFixed(2)} KB)`;

        previewCSVFile(file, previewId, previewContentId, fileType);
    } else {
        document.getElementById(fileInfoId).textContent = '未选择文件';
        document.getElementById(previewId).style.display = 'none';

        // 清空对应的列名数据
        if (fileType === 'A') {
            columnsA = [];
        } else {
            columnsB = [];
        }
        updateMatchRulesSection();
    }
}

// 修改预览函数，添加列名提取
function previewCSVFile(file, previewId, previewContentId, fileType) {
    const reader = new FileReader();

    reader.onload = function(e) {
        try {
            const text = e.target.result;
            const lines = text.split('\n').filter(line => line.trim() !== '');

            if (lines.length === 0) {
                showPreviewError(previewContentId, '文件为空');
                return;
            }

            // 提取列名
            const headers = parseCSVLine(lines[0]);
            if (fileType === 'A') {
                columnsA = headers;
            } else {
                columnsB = headers;
            }

            const previewLines = lines.slice(0, 10);
            const table = createPreviewTable(previewLines);

            document.getElementById(previewContentId).innerHTML = table;
            document.getElementById(previewId).style.display = 'block';

            // 更新匹配规则区域
            updateMatchRulesSection();

        } catch (error) {
            showPreviewError(previewContentId, '文件读取失败: ' + error.message);
        }
    };

    reader.onerror = function() {
        showPreviewError(previewContentId, '文件读取失败');
    };

    reader.readAsText(file, 'UTF-8');
}

// 更新匹配规则区域
function updateMatchRulesSection() {
    const matchRulesSection = document.getElementById('matchRulesSection');

    if (columnsA.length > 0 && columnsB.length > 0) {
        matchRulesSection.style.display = 'block';
        updateFieldSelects();
    } else {
        matchRulesSection.style.display = 'none';
    }
}

// 更新字段选择下拉框
function updateFieldSelects() {
    updateSelectOptions('primaryKeyA', columnsA);
    updateSelectOptions('primaryKeyB', columnsB);

    // 更新所有附加规则的下拉框
    const additionalRules = document.querySelectorAll('.additional-rule-item');
    additionalRules.forEach(rule => {
        const selectA = rule.querySelector('.rule-field-a');
        const selectB = rule.querySelector('.rule-field-b');
        if (selectA && selectB) {
            updateSelectOptions(selectA.id, columnsA);
            updateSelectOptions(selectB.id, columnsB);
        }
    });
}

// 更新下拉框选项
function updateSelectOptions(selectId, columns) {
    const select = document.getElementById(selectId);
    if (!select) return;

    const currentValue = select.value;
    select.innerHTML = `<option value="">请选择字段</option>`;

    columns.forEach(column => {
        const option = document.createElement('option');
        option.value = column;
        option.textContent = column;
        if (column === currentValue) {
            option.selected = true;
        }
        select.appendChild(option);
    });
}

// 添加匹配规则
function addMatchRule() {
    additionalRuleCount++;
    const ruleId = `rule_${additionalRuleCount}`;

    const ruleHtml = `
        <div class="additional-rule-item" id="${ruleId}">
            <select id="ruleFieldA_${additionalRuleCount}" class="field-select rule-field-a">
                <option value="">选择文件A字段</option>
            </select>
            <span class="mapping-arrow">↔</span>
            <select id="ruleFieldB_${additionalRuleCount}" class="field-select rule-field-b">
                <option value="">选择文件B字段</option>
            </select>
            <button type="button" class="remove-rule-btn" onclick="removeMatchRule('${ruleId}')">删除</button>
        </div>
    `;

    document.getElementById('rulesList').insertAdjacentHTML('beforeend', ruleHtml);

    // 更新新添加的下拉框
    updateSelectOptions(`ruleFieldA_${additionalRuleCount}`, columnsA);
    updateSelectOptions(`ruleFieldB_${additionalRuleCount}`, columnsB);
}

// 删除匹配规则
function removeMatchRule(ruleId) {
    const ruleElement = document.getElementById(ruleId);
    if (ruleElement) {
        ruleElement.remove();
    }
}

// 获取匹配规则配置
function getMatchRules() {
    const rules = {
        primaryKey: {
            fieldA: document.getElementById('primaryKeyA').value,
            fieldB: document.getElementById('primaryKeyB').value
        },
        additionalRules: [],
        options: {
            caseSensitive: document.getElementById('caseSensitive').checked,
            trimSpaces: document.getElementById('trimSpaces').checked,
            exactMatch: document.getElementById('exactMatch').checked
        }
    };

    // 收集附加规则
    const additionalRules = document.querySelectorAll('.additional-rule-item');
    additionalRules.forEach(rule => {
        const fieldA = rule.querySelector('.rule-field-a').value;
        const fieldB = rule.querySelector('.rule-field-b').value;

        if (fieldA && fieldB) {
            rules.additionalRules.push({
                fieldA: fieldA,
                fieldB: fieldB
            });
        }
    });

    return rules;
}

// 验证匹配规则
function validateMatchRules() {
    const rules = getMatchRules();

    if (!rules.primaryKey.fieldA || !rules.primaryKey.fieldB) {
        alert('请选择主键匹配字段！');
        return false;
    }

    return true;
}

// 生成预览表格
function createPreviewTable(lines) {
    if (lines.length === 0) return '<div class="preview-error">无数据</div>';

    let html = '<table class="preview-table">';

    // 表头
    const headers = parseCSVLine(lines[0]);
    html += '<thead><tr>';
    headers.forEach(header => {
        html += `<th>${escapeHtml(header)}</th>`;
    });
    html += '</tr></thead>';

    // 数据行
    html += '<tbody>';
    for (let i = 1; i < lines.length; i++) {
        const cells = parseCSVLine(lines[i]);
        html += '<tr>';

        // 确保每行的列数与表头一致
        for (let j = 0; j < headers.length; j++) {
            const cellValue = cells[j] || '';
            html += `<td>${escapeHtml(cellValue)}</td>`;
        }
        html += '</tr>';
    }
    html += '</tbody></table>';

    return html;
}

// 解析CSV行
function parseCSVLine(line) {
    const result = [];
    let current = '';
    let inQuotes = false;

    for (let i = 0; i < line.length; i++) {
        const char = line[i];

        if (char === '"') {
            inQuotes = !inQuotes;
        } else if (char === ',' && !inQuotes) {
            result.push(current.trim());
            current = '';
        } else {
            current += char;
        }
    }

    result.push(current.trim());
    return result;
}

// 显示预览错误
function showPreviewError(previewContentId, message) {
    document.getElementById(previewContentId).innerHTML =
        `<div class="preview-error">${escapeHtml(message)}</div>`;
    document.getElementById(previewContentId.replace('Content', '')).style.display = 'block';
}

// 转义HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 修改匹配处理函数，包含匹配规则
function processMatch() {
    if (!validateMatchRules()) {
        return;
    }

    const matchRules = getMatchRules();
    console.log('匹配规则:', matchRules);

    // 这里可以将匹配规则发送到后端
    // 或者在前端进行匹配处理

    alert('匹配规则配置完成！\n主键字段: ' +
          matchRules.primaryKey.fieldA + ' ↔ ' + matchRules.primaryKey.fieldB +
          '\n附加规则数量: ' + matchRules.additionalRules.length);
}
