<template>
  <div class="q-pa-md q-gutter-sm">    <q-editor
      v-model="editorContent"
      :dense="$q.screen.lt.md"
      min-height="5rem"
      :toolbar="[
        ['bold', 'italic', 'underline', 'strike', 'fontSize', 'left', 'center', 'right', 'quote', 'unordered', 'ordered', 'link', 'undo', 'redo']
      ]"
      @focus="handleFocus"
      @blur="handleBlur"
      :fonts="{
        arial: 'Arial',
        arial_black: 'Arial Black',
        comic_sans: 'Comic Sans MS',
        courier_new: 'Courier New',
        impact: 'Impact',
        lucida_grande: 'Lucida Grande',
        times_new_roman: 'Times New Roman',
        verdana: 'Verdana'
      }"
    />
    </div>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'WriteBoard',
  props: {
    modelValue: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue', 'focus', 'blur'],
  setup(props, { emit }) {
    const editorContent = ref(props.modelValue || '');
    
    // 监听父组件传入的值变化
    watch(() => props.modelValue, (newVal) => {
      if (newVal !== editorContent.value) {
        editorContent.value = newVal;
      }
    });
    
    // 监听编辑器内容变化，向父组件发送更新
    watch(editorContent, (newVal) => {
      emit('update:modelValue', newVal);
    });
    
    // 焦点事件处理
    const handleFocus = () => {
      emit('focus');
    };
    
    const handleBlur = () => {
      emit('blur');
    };
    
    return {
      editorContent,
      handleFocus,
      handleBlur
    }
  }
}
</script>
