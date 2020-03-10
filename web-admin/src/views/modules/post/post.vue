<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-row type="flex" class="row-bg">
      <el-form-item>
        <el-input v-model="dataForm.id" placeholder="帖子ID" clearable ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.userId" placeholder="用户ID" clearable></el-input>
      </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.content" placeholder="关键字" clearable></el-input>
        </el-form-item>
      <el-form-item>
      <el-select v-model="dataForm.tagId" clearable placeholder="标签" :change="selectTagId" width="small">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.tagName"
          :value="item.id">
        </el-option>
      </el-select>
    </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.shieldStatus" clearable placeholder="屏蔽状态" :change="selectTagId">
          <el-option
            v-for="item in items"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">查询</el-button>
          <!--<el-button v-if="isAuth('post:post:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>-->
        </el-form-item>
      </el-row>
      <el-form-item>
        <el-button type="success" @click="topPostHandle()">置顶帖子列表</el-button>
        <el-button v-if="isAuth('post:post:save')" type="primary" @click="addOrUpdateHandle()">新增置顶帖子</el-button>
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
        prop="userId"
        header-align="center"
        align="center"
        label="用户ID">
      </el-table-column>
      <!-- <el-table-column
        prop="postUserName"
        header-align="center"
        align="center"
        label="用户名">
      </el-table-column> -->
      <!-- <el-table-column
        prop="nickName"
        header-align="center"
        align="center"
        label="昵称">
      </el-table-column> -->
      <el-table-column
        prop="publishStatus"
        header-align="center"
        align="center"
        label="发布状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.publishStatus === 0" size="small" type="warning">未发布</el-tag>
          <el-tag v-else size="small">已发布</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="shieldStatus"
        header-align="center"
        align="center"
        label="屏蔽状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.shieldStatus === 1" size="small" type="warning">已屏蔽</el-tag>
          <el-tag v-else size="small">未屏蔽</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="deleted"
        header-align="center"
        align="center"
        label="删除状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.deleted === 1" size="small" type="warning">已删除</el-tag>
          <el-tag v-else size="small">未删除</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="commentFlag"
        header-align="center"
        align="center"
        label="是否可评论">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.commentFlag === 1" size="small" type="warning">否</el-tag>
          <el-tag v-else size="small">是</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="postTagName"
        header-align="center"
        align="center"
        label="标签名称">
      </el-table-column>
      <el-table-column
        prop="commentCount"
        header-align="center"
        align="center"
        label="评论数"
        width="70">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="commentHandle(scope.row.id)">{{scope.row.commentCount}}</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="likeCount"
        header-align="center"
        align="center"
        label="点赞数"
        width="70">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="发帖时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="infoHandle(scope.row.id)">查看</el-button>
           <el-button type="text" v-if="scope.row.shieldStatus===0" size="small" @click="shield(scope.row.id,1)">屏蔽</el-button>
          <el-button type="text" v-if="scope.row.shieldStatus===1" size="small" @click="shield(scope.row.id,0)">解除屏蔽</el-button>
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
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    <!-- 查看-->
    <get-info v-if="infoVisible" ref="getInfo" @refreshDataList="getDataList"></get-info>
      <!-- 弹窗, 置顶帖子 -->
    <top-post v-if="topVisible" ref="topPost"></top-post>
    <!--帖子评论-->
    <post-reply v-if="postReplyVisible" ref="postReply"></post-reply>
  </div>
</template>

<script>
  import AddOrUpdate from './post-add-or-update'
  import GetInfo from './post-info'
  import TopPost from './post-top'
  import PostReply from './post-reply'
  export default {
    data () {
      return {
        dataForm: {
          id: '',
          userId: '',
          tagId: '',
          shieldStatus: '',
          content: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        infoVisible: false,
        topVisible: false,
        postReplyVisible: false,
        options: [],
        items: [
          {
            id: 0,
            name: '未屏蔽'
          },
          {
            id: 1,
            name: '已屏蔽'
          }
        ]
      }
    },
    components: {
      AddOrUpdate,
      GetInfo,
      TopPost,
      PostReply
    },
    activated () {
      this.getDataList()
      if (this.$route.params.id) {
        var type = this.$route.params.type
        if (type === 1) {
          this.infoHandle(this.$route.params.id)
        } else {
          this.commentHandle(null, this.$route.params.id)
        }
      }
    },
    created () {
      this.getQuerList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/post/post/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'id': this.dataForm.id,
            'tagId': this.dataForm.tagId,
            'shieldStatus': this.dataForm.shieldStatus,
            'osType': 0,
            'userId': this.dataForm.userId,
            'content': this.dataForm.content
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
      getQuerList () {
        this.$http({
          url: this.$http.adornUrl('/post/tag/querList'),
          method: 'get'
        }).then(data => {
          this.options = data.data.list
        })
      },
      selectTagId (item) {
        console.log(item)
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
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
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
            url: this.$http.adornUrl('/post/post/delete'),
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

      shield (id, shieldStatus) {
        this.$confirm(`确定进行[${shieldStatus === 0 ? '解除屏蔽' : '屏蔽'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/post/post/update'),
            method: 'post',
            data: this.$http.adornParams({
              'id': id,
              'shieldStatus': shieldStatus
            })
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
      infoHandle (id) {
        this.infoVisible = true
        this.$nextTick(() => {
          this.$refs.getInfo.init(id)
        })
      },
      // 置顶帖子
      topPostHandle () {
        this.topVisible = true
        this.$nextTick(() => {
          this.$refs.topPost.init()
        })
      },
      commentHandle (postId, id) {
        this.postReplyVisible = true
        this.$nextTick(() => {
          this.$refs.postReply.init(postId, id)
        })
      }
    }
  }
</script>
