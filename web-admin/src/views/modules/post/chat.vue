<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.userId" placeholder="用户ID" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.chatId" placeholder="会话ID" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.postId" placeholder="帖子ID" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('post:chat:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
        prop="chatId"
        header-align="center"
        align="center"
        label="会话ID">
      </el-table-column>
      <el-table-column
        prop="postId"
        header-align="center"
        align="center"
        label="帖子ID">
      </el-table-column>
      <el-table-column
        prop="fromUid"
        header-align="center"
        align="center"
        label="发送人ID">
      </el-table-column>
      <el-table-column
        prop="toUid"
        header-align="center"
        align="center"
        label="接收人ID">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="会话状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="warning">关闭</el-tag>
          <el-tag v-if="scope.row.status === 1" size="small" type="danger">举报</el-tag>
          <el-tag v-else size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="开始时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('post:message:list')" type="text" size="small" @click="detailChatHandle(scope.row.chatId)">会话内容</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.chatId)">删除</el-button>
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
    <!-- 弹窗, 日志列表 -->
    <message v-if="messageVisible" ref="message"></message>
  </div>
</template>

<script>
  import Message from './message'
  export default {
    data () {
      return {
        dataForm: {
          chatId: '',
          userId: '',
          postId: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        messageVisible: false
      }
    },
    components: {
      Message
    },
    activated () {
      this.getDataList()
      if (this.$route.params.id) {
        this.detailChatHandle(this.$route.params.id)
      }
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/post/chat/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'osType': 0,
            'chatId': this.dataForm.chatId,
            'userId': this.dataForm.userId,
            'postId': this.dataForm.postId
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
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/post/chat/delete'),
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
      // 消息列表
      detailChatHandle (id) {
        this.messageVisible = true
        this.$nextTick(() => {
          this.$refs.message.init(id)
        })
      }
    }
  }
</script>
