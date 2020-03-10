<template>
  <el-dialog
    :title="!dataForm.msgId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" :disabled="true" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="魔瓶id" prop="postId">
        <el-input v-model="dataForm.postId" placeholder="魔瓶id"></el-input>
      </el-form-item>
      <el-form-item label="发送者id" prop="fromUid">
        <el-input v-model="dataForm.fromUid" placeholder="发送者id"></el-input>
      </el-form-item>
      <el-form-item label="接收者id" prop="toUid">
        <el-input v-model="dataForm.toUid" placeholder="接收者id"></el-input>
      </el-form-item>
      <el-form-item label="发送者昵称" prop="fromNickName">
        <el-input v-model="dataForm.fromNickName" placeholder="发送者昵称"></el-input>
      </el-form-item>
      <el-form-item label="接收者昵称" prop="toNickName">
        <el-input v-model="dataForm.toNickName" placeholder="接收者昵称"></el-input>
      </el-form-item>
      <el-form-item label="1 文字 2图片 3评论通知  4点赞通知" prop="msgType">
        <el-input v-model="dataForm.msgType" placeholder="1 文字 2图片 3评论通知  4点赞通知"></el-input>
      </el-form-item>
      <el-form-item label="状态 0：未发送  1：已发送" prop="status">
        <el-input v-model="dataForm.status" placeholder="状态 0：未发送  1：已发送"></el-input>
      </el-form-item>
      <el-form-item label="消息内容" prop="content">
        <el-input v-model="dataForm.content" placeholder="消息内容"></el-input>
      </el-form-item>
      <el-form-item label="是否屏蔽 0否 1是" prop="shieldStatus">
        <el-input v-model="dataForm.shieldStatus" placeholder="是否屏蔽 0否 1是"></el-input>
      </el-form-item>
      <el-form-item label="图片地址" prop="imageUrl">
        <el-input v-model="dataForm.imageUrl" placeholder="图片地址"></el-input>
      </el-form-item>
      <el-form-item label="" prop="jpushId">
        <el-input v-model="dataForm.jpushId" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="" prop="updateTime">
        <el-input v-model="dataForm.updateTime" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          msgId: 0,
          postId: '',
          fromUid: '',
          toUid: '',
          fromNickName: '',
          toNickName: '',
          msgType: '',
          status: '',
          content: '',
          shieldStatus: '',
          imageUrl: '',
          jpushId: '',
          updateTime: '',
          createTime: ''
        },
        dataRule: {
          postId: [
            { required: true, message: '魔瓶id不能为空', trigger: 'blur' }
          ],
          fromUid: [
            { required: true, message: '发送者id不能为空', trigger: 'blur' }
          ],
          toUid: [
            { required: true, message: '接收者id不能为空', trigger: 'blur' }
          ],
          fromNickName: [
            { required: true, message: '发送者昵称不能为空', trigger: 'blur' }
          ],
          toNickName: [
            { required: true, message: '接收者昵称不能为空', trigger: 'blur' }
          ],
          msgType: [
            { required: true, message: '1 文字 2图片 3评论通知  4点赞通知不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态 0：未发送  1：已发送不能为空', trigger: 'blur' }
          ],
          content: [
            { required: true, message: '消息内容不能为空', trigger: 'blur' }
          ],
          shieldStatus: [
            { required: true, message: '是否屏蔽 0否 1是不能为空', trigger: 'blur' }
          ],
          imageUrl: [
            { required: true, message: '图片地址不能为空', trigger: 'blur' }
          ],
          jpushId: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (msgId) {
        this.dataForm.msgId = msgId || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.msgId) {
            this.$http({
              url: this.$http.adornUrl(`/post/message/info/${this.dataForm.msgId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.postId = data.message.postId
                this.dataForm.fromUid = data.message.fromUid
                this.dataForm.toUid = data.message.toUid
                this.dataForm.fromNickName = data.message.fromNickName
                this.dataForm.toNickName = data.message.toNickName
                this.dataForm.msgType = data.message.msgType
                this.dataForm.status = data.message.status
                this.dataForm.content = data.message.content
                this.dataForm.shieldStatus = data.message.shieldStatus
                this.dataForm.imageUrl = data.message.imageUrl
                this.dataForm.jpushId = data.message.jpushId
                this.dataForm.updateTime = data.message.updateTime
                this.dataForm.createTime = data.message.createTime
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/post/message/${!this.dataForm.msgId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'msgId': this.dataForm.msgId || undefined,
                'postId': this.dataForm.postId,
                'fromUid': this.dataForm.fromUid,
                'toUid': this.dataForm.toUid,
                'fromNickName': this.dataForm.fromNickName,
                'toNickName': this.dataForm.toNickName,
                'msgType': this.dataForm.msgType,
                'status': this.dataForm.status,
                'content': this.dataForm.content,
                'shieldStatus': this.dataForm.shieldStatus,
                'imageUrl': this.dataForm.imageUrl,
                'jpushId': this.dataForm.jpushId,
                'updateTime': this.dataForm.updateTime,
                'createTime': this.dataForm.createTime
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
