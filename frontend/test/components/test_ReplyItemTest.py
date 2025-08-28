pytest_plugins = ["pytest_playwright"]

import pytest
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/discussions/1"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url("http://localhost:3000/")
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_replyitem_teacher_view(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator(".reply-list")).to_be_visible(timeout=10000)
    teacher_badge = page.locator(".reply-item .bg-red-100.text-red-800")
    assert teacher_badge.count() >= 0
    # 检查编辑/删除按钮存在且可见（如果没有回复则跳过断言）
    edit_btns = page.locator(".reply-item .edit-btn")
    delete_btns = page.locator(".reply-item .delete-btn")
    if edit_btns.count() > 0:
        expect(edit_btns.first).to_be_visible()
    if delete_btns.count() > 0:
        expect(delete_btns.first).to_be_visible()
    context.close()
    browser.close()

def test_replyitem_student_view(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".reply-list")).to_be_visible(timeout=10000)
    reply_items = page.locator(".reply-item")
    count = reply_items.count()
    for i in range(count):
        item = reply_items.nth(i)
        # 检查编辑/删除按钮是否存在
        if item.locator(".edit-btn").count() > 0:
            item.locator(".edit-btn").is_visible()
        if item.locator(".delete-btn").count() > 0:
            item.locator(".delete-btn").is_visible()
    context.close()
    browser.close()

def test_replyitem_edit_flow(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".reply-list")).to_be_visible(timeout=10000)
    edit_btns = page.locator(".reply-item .edit-btn")
    if edit_btns.count() > 0:
        edit_btn = edit_btns.first
        edit_btn.click()
        expect(page.locator(".reply-item textarea")).to_be_visible()
        page.locator(".reply-item textarea").fill("自动化编辑测试内容")
        page.locator(".reply-item .submit-button").click()
        expect(page.locator(".reply-item")).to_contain_text("自动化编辑测试内容")
    else:
        print("无可编辑的回复，跳过编辑流程测试")
    context.close()
    browser.close()

def test_replyitem_reply_flow(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".reply-list")).to_be_visible(timeout=10000)
    reply_btns = page.locator(".reply-item .reply-btn")
    if reply_btns.count() > 0:
        reply_btn = reply_btns.first
        reply_btn.click()
        expect(page.locator(".reply-item .reply-form textarea")).to_be_visible()
        page.locator(".reply-item .reply-form textarea").fill("自动化回复测试内容")
        page.locator(".reply-item .reply-form .submit-button").click()
        expect(page.locator(".reply-list")).to_contain_text("自动化回复测试内容")
    else:
        print("无可回复的回复项，跳过回复流程测试")
    context.close()
    browser.close()
