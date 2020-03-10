<template>
    <el-dialog
    title="帖子评论"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="75%">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <!-- <el-form-item>
        <el-input v-model="dataForm.id" placeholder="参数名" clearable></el-input>
      </el-form-item> -->
      <el-form-item>
        <!-- <el-button v-if="isAuth('post:postreply:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button> -->
        <el-button v-if="isAuth('post:postreply:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
        prop="id"
        header-align="center"
        align="center"
        label="ID">
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
        label="用户id">
      </el-table-column>
      <el-table-column
        prop="toUid"
        header-align="center"
        align="center"
        label="目标用户id">
      </el-table-column>
      <el-table-column
        prop="content"
        header-align="center"
        align="center"
        label="评论内容">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="评论时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: []
      }
    },
    active () {
      if (this.$route.params.id) {
        this.getDataList(null, this.$route.params.id)
      }
    },
    methods: {
      init (postId, id) {
        this.visible = true
        this.getDataList(postId, id)
      },
      // 获取数据列表
      getDataList (postId, id) {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/post/postreply/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'postId': postId,
            'id': id
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
            url: this.$http.adornUrl('/post/postreply/delete'),
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
      }
    }
  }
</script>
