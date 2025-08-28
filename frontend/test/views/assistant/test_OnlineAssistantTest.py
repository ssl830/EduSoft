pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

LOGIN_URL = "http://localhost:3000/login"
ASSISTANT_URL = "http://localhost:3000/assistant"
USER_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    # 登录后再跳转目标页
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_online_assistant_basic_flow(playwright):
    browser, context, page = login_and_get_page(playwright, USER_CREDENTIALS, ASSISTANT_URL)
    # 页面加载
    expect(page.locator(".assistant-container")).to_be_visible(timeout=10000)
    # 课程下拉框、输入框、发送按钮
    expect(page.locator(".course-select-bar")).to_be_visible()
    expect(page.locator(".input-area input")).to_be_visible()
    expect(page.locator(".input-area button")).to_be_visible()
    # 新建聊天
    page.locator(".new-chat-btn").click()
    expect(page.locator(".session-item.active")).to_be_visible()
    # 输入并发送消息
    page.locator(".input-area input").fill("AI助手测试问题")
    page.locator(".input-area button").click()
    # 等待AI回复
    expect(page.locator(".chat-bubble.assistant")).to_be_visible(timeout=15000)
    # 会话列表切换
    session_items = page.locator(".session-item")
    if session_items.count() > 1:
        session_items.nth(0).click()
        expect(page.locator(".session-item.active")).to_be_visible()
    # 删除会话
    del_btns = page.locator(".session-item .delete-btn")
    if del_btns.count() > 0:
        del_btns.first.click()
        # 确认弹窗
        page.on("dialog", lambda dialog: dialog.accept())
    context.close()
    browser.close()
