<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.toUid" placeholder="被举报人Id" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.fromUid" placeholder="举报人Id" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.itemType" clearable placeholder="举报类型"  width="small">
          <el-option
            v-for="item in options"
            :key="item.itemType"
            :label="item.itemName"
            :value="item.itemType">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button v-if="isAuth('support:tipofflog:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
        label="ID"
        v-if="false">
      </el-table-column>
      <el-table-column
        prop="itemId"
        header-align="center"
        align="center"
        label="举报目标id">
      </el-table-column>
      <el-table-column
        prop="itemType"
        header-align="center"
        align="center"
        label="举报目标类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.itemType === 1" size="small">帖子</el-tag>
          <el-tag v-if="scope.row.itemType === 2" size="small">评论</el-tag>
          <el-tag v-if="scope.row.itemType === 3" size="small">会话</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="toUserId"
        header-align="center"
        align="center"
        label="被举报人id">
      </el-table-column>
      <el-table-column
        prop="userId"
        header-align="center"
        align="center"
        label="举报人Id">
      </el-table-column>
      <el-table-column
        prop="ip"
        header-align="center"
        align="center"
        label="举报人ip">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="举报时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
          <router-link v-if="scope.row.itemType===1"  :to="{ name:'post-post',params:{id:scope.row.itemId,type:1}}">
            <el-button type="text" size="small">查看帖子</el-button>
          </router-link>
          <router-link v-if="scope.row.itemType===2"  :to="{ name:'post-post',params:{id:scope.row.itemId,type:2}}">
            <el-button type="text" size="small">查看评论</el-button>
          </router-link>
          <router-link v-if="scope.row.itemType===3"  :to="{ name:'post-chat',params:{id:scope.row.itemId}}">
            <el-button type="text" size="small">查看对话</el-button>
          </router-link>
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
  </div>
</template>

<script>
  export default {
    data () {
      return {
        dataForm: {
          key: '',
          toUid: '',
          fromUid: '',
          itemType: ''
        },
        options: [
          {
            itemType: 1,
            itemName: '帖子'
          },
          {
            itemType: 2,
            itemName: '评论'
          },
          {
            itemType: 3,
            itemName: '会话'
          }
        ],
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: []
      }
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/support/tipofflog/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'toUid': this.dataForm.toUid,
            'fromUid': this.dataForm.fromUid,
            'itemType': this.dataForm.itemType
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
            url: this.$http.adornUrl('/support/tipofflog/delete'),
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
