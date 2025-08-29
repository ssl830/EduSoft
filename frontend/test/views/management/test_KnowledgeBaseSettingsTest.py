pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/settings/knowledge-base"
LOGIN_URL = "http://localhost:3000/login"
TUTOR_CREDENTIALS = {"userId": "A001", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    if goto_url:
        if page.url.startswith("http://localhost:3000/login?redirect="):
            page.goto(goto_url)
        else:
            page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_kb_settings_render(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".settings-page")).to_be_visible(timeout=10000)
    expect(page.locator(".settings-title").first).to_contain_text("私密知识库设置")
    expect(page.locator("input[placeholder='例如：D:/MyKnowledgeBase']")).to_be_visible()
    # 只检查第一个未禁用且可见的“保存设置”按钮
    save_btns = page.locator("button:has-text('保存设置')")
    found_visible = False
    for i in range(save_btns.count()):
        btn = save_btns.nth(i)
        if btn.is_visible() and btn.is_enabled():
            expect(btn).to_be_visible()
            found_visible = True
            break
    assert found_visible, "未找到可见且可用的保存设置按钮"
    expect(page.locator("button:has-text('恢复默认')")).to_be_visible()
    expect(page.locator(".upload-row")).to_be_visible()
    context.close()
    browser.close()

def test_kb_settings_save_path(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".settings-page")).to_be_visible(timeout=10000)
    input_box = page.locator("input[placeholder='例如：D:/MyKnowledgeBase']")
    input_box.fill("D:/自动化测试知识库")
    # 点击第一个未禁用的“保存设置”按钮
    save_btns = page.locator("button:has-text('保存设置')")
    for i in range(save_btns.count()):
        btn = save_btns.nth(i)
        if btn.is_enabled():
            btn.click()
            break
    # 检查通知或路径变更
    expect(page.locator(".q-notification")).to_be_visible(timeout=5000)
    context.close()
    browser.close()

def test_kb_settings_reset(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".settings-page")).to_be_visible(timeout=10000)
    page.locator("button:has-text('恢复默认')").click()
    expect(page.locator(".q-notification")).to_be_visible(timeout=5000)
    context.close()
    browser.close()

def test_kb_settings_union_select(playwright):
    browser, context, page = login_and_get_page(playwright, TUTOR_CREDENTIALS, BASE_URL)
    expect(page.locator(".settings-page")).to_be_visible(timeout=10000)
    # 选择联合知识库
    first_checkbox = page.locator(".selectable-kb-item .q-checkbox").first
    if first_checkbox.count() > 0 and first_checkbox.is_visible():
        first_checkbox.click()
        # 点击第一个未禁用的“��存设置”按钮
        save_btns = page.locator("button:has-text('保存设置')")
        for i in range(save_btns.count()):
            btn = save_btns.nth(i)
            if btn.is_enabled():
                btn.click()
                break
        expect(page.locator(".q-notification")).to_be_visible(timeout=5000)
    context.close()
    browser.close()
