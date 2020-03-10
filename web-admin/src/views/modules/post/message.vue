<template>
    <el-dialog
    title="消息列表"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="75%">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <!-- <el-form-item>
        <el-input v-model="dataForm.key" placeholder="参数名" clearable></el-input>
      </el-form-item> -->
      <el-form-item>
        <!-- <el-button @click="getDataList()">查询</el-button> -->
        <!-- <el-button v-if="isAuth('post:message:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button> -->
        <el-button v-if="isAuth('post:message:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="msgId"
        header-align="center"
        align="center"
        label="ID"
        v-if="false">
      </el-table-column>
      <el-table-column
        prop="postContent"
        header-align="center"
        align="center"
        label="对应帖子">
      </el-table-column>
      <el-table-column
        prop="fromNickName"
        header-align="center"
        align="center"
        label="发送者昵称">
      </el-table-column>
      <el-table-column
        prop="toNickName"
        header-align="center"
        align="center"
        label="接收者昵称">
      </el-table-column>
      <el-table-column
        prop="msgType"
        header-align="center"
        align="center"
        label="消息类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.msgType === 1" size="small" type="success">文字</el-tag>
          <el-tag v-if="scope.row.msgType === 2" size="small" type="success">图片</el-tag>
          <el-tag v-if="scope.row.msgType === 3" size="small" type="success">评论通知</el-tag>
          <el-tag v-if="scope.row.msgType === 4" size="small" type="success">点赞通知</el-tag>
          <el-tag v-if="scope.row.msgType === 5" size="small" type="success">会话关闭</el-tag>
          <el-tag v-if="scope.row.msgType === 6" size="small" type="success">会话举报</el-tag>
          <el-tag v-if="scope.row.msgType === 7" size="small" type="success">会话开启</el-tag>
          <el-tag v-if="scope.row.msgType === 8" size="small" type="success">禁言提醒</el-tag>
          <el-tag v-if="scope.row.msgType === 9" size="small" type="success">不同设备登陆通知</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="content"
        header-align="center"
        align="center"
        label="消息内容">
        <template slot-scope="scope">
          <img v-if ="scope.row.msgType ===2" :src="scope.row.content" width="80" height="80"/>
          <span v-else>{{scope.row.content}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="创建时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <!-- <el-button type="text" size="small" @click="infoHandle(scope.row.msgId)">查看</el-button>
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.msgId)">修改</el-button> -->
          <el-button type="text" size="small" @click="deleteHandle(scope.row.msgId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    <!-- 查看-->
    <get-info v-if="infoVisible" ref="getInfo" @refreshDataList="getDataList"></get-info>
  </el-dialog>
</template>

<script>
  import AddOrUpdate from './message-add-or-update'
  import GetInfo from './message-info'
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          key: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        infoVisible: false
      }
    },
    components: {
      AddOrUpdate,
      GetInfo
    },
    activated () {
      this.getDataList()
    },
    methods: {
      init (chatId) {
        this.visible = true
        this.pageIndex = 1
        this.chatId = chatId
        this.getDataList(this.chatId)
      },
      // 获取数据列表
      getDataList (chatId) {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/post/message/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'tagName': this.dataForm.tagName,
            'chatId': this.chatId
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 新增 / 修改
      addOrUpdateHandle (msgId) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(msgId)
        })
      },
      // 删除
      deleteHandle (msgId) {
        var ids = msgId ? [msgId] : this.dataListSelections.map(item => {
          return item.msgId
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${msgId ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/post/message/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {})
      },
      // 查看
      infoHandle (msgId) {
        this.infoVisible = true
        this.$nextTick(() => {
          this.$refs.getInfo.init(msgId)
        })
      }
    }
  }
</script>
